package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.Comment;
import com.nowcoder.community2.service.CommentService;
import com.nowcoder.community2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
//        前端会隐式的传入Comment实体的参数entityType,entityId,targetId等。。
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

//        发完评论之后，重定向到帖子详情页面
        return "redirect:/discuss/detail/" + discussPostId;
    }
}



