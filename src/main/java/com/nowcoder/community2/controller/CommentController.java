package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.Comment;
import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.Event;
import com.nowcoder.community2.event.EventProducer;
import com.nowcoder.community2.service.CommentService;
import com.nowcoder.community2.service.DiscussPostService;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.HostHolder;
import com.nowcoder.community2.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
//        前端会隐式的传入Comment实体的参数entityType,entityId,targetId等。。
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

//        加更：添加评论的同时，添加事件通知
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
//        如果事件发生的实体是帖子或者评论，那么还需要添加实体对应用户的id
        if(comment.getEntityType() == ENTITY_TYPE_POST){
//            找出评论的目标帖子所对应的用户id
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
//        处理事件
        eventProducer.fireEvent(event);

//        加更，如果是针对帖子的评论，那么同时也要修改es中的数据（因为评论数量变了
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

//            将该帖子id放入待更新权重的key中
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

//        发完评论之后，重定向到帖子详情页面
        return "redirect:/discuss/detail/" + discussPostId;
    }
}



