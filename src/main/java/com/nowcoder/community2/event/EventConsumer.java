package com.nowcoder.community2.event;


import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.Event;
import com.nowcoder.community2.entity.Message;
import com.nowcoder.community2.service.DiscussPostService;
import com.nowcoder.community2.service.ElasticsearchService;
import com.nowcoder.community2.service.MessageService;
import com.nowcoder.community2.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private DiscussPostService discussPostService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if(record == null || record.value() == null){
            logger.error("消息内容为空！");
            return;
        }
//        取出消息队列中的内容并直接转化成我们想要的格式
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }
//        最后要向Message表中添加对应的数据
        Message message = new Message();
//        发消息的永远是1，表示系统用户
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

//        最后message的内容content就是最后我们想要在信息页面显示的信息，内容信息很多，所以通过一个Map接收
        Map<String, Object> content = new HashMap<>();
//        需要知道谁触发的这个事件
        content.put("userId", event.getUserId());
//        事件是什么类型
        content.put("entityType", event.getEntityType());
//        事件对应类型的id（用来点击查看进行跳转？
        content.put("entityId", event.getEntityId());
//        如果事件对应的data不为空，说明有一些特殊信息，全部添加到content中
//        如:点赞和评论事件还需要添加一个postId,表明是哪一个帖子
        if(!event.getData().isEmpty()){
            for(Map.Entry<String, Object> map : event.getData().entrySet()){
                content.put(map.getKey(), map.getValue());
            }
        }

//        把content转化成JSON字符串放到content中
        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);
    }

    /**
     * 处理发帖事件
     * @param record
     */
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }
//        根据事件中的帖子id找出对应帖子，并放入到es即可
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    //    删除
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDelete(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }
//        从es中删除帖子
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }
}
