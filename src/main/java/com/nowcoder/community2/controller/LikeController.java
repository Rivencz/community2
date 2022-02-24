package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.Event;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.event.EventProducer;
import com.nowcoder.community2.service.LikeService;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.CommunityUtil;
import com.nowcoder.community2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 点赞
     * @param entityType 实体类型（帖子或评论
     * @param entityId 实体id
     * @param entityUserId 实体对应用户的id
     * @param postId 当前帖子id，处理事件业务时需要使用（后续加的）
     * @return
     */
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
//        后续我们会统一管理权限
        User user = hostHolder.getUser();

        likeService.like(user.getId(), entityType, entityId, entityUserId);
        Map<String,Object> map = new HashMap<>();
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

//        加更：进行点赞的同时，创建事件通知
        if(likeStatus == 1){
            Event event = new Event()
                    .setUserId(hostHolder.getUser().getId())
                    .setTopic(TOPIC_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }


        return CommunityUtil.getJSONString(0, null, map);
    }
}
