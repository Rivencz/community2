package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.*;
import com.nowcoder.community2.event.EventProducer;
import com.nowcoder.community2.service.CommentService;
import com.nowcoder.community2.service.DiscussPostService;
import com.nowcoder.community2.service.LikeService;
import com.nowcoder.community2.service.UserService;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.CommunityUtil;
import com.nowcoder.community2.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 添加帖子
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "请先登录！");
        }
//        这个是自己写的异常处理，后续会统一处理异常
//        if(StringUtils.isBlank(title) || StringUtils.isBlank(content)){
//            return CommunityUtil.getJSONString(403, "标题或内容不能为空!");
//        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

//        加更：触发发帖事件（添加帖子的同时将它放入到es数据库中）
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    /**
     * 实现帖子详情页功能
     *
     * @param discussPostId
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String findDiscussPostById(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        /*
        一共给model传递了三个元素
        1.post（帖子
        2.user（帖子作者
        3.comments（帖子评论的list集合
         */
//        帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(post.getUserId());
//        把帖子和用户都传给前端界面进行使用
        model.addAttribute("post", post);
        model.addAttribute("user", user);

//        加更1：显示当前帖子的点赞数量，放入model中
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

//        分页显示评论的相关设置
//        每页显示5条评论
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

//        分页找出当前帖子的所有评论
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            /*
//            每个Comment包含四个元素，通过Map类型的CommentVo来依次将他们添加
            1.评论实体comment
            2.评论作者user（用于头像显示等）
            3.评论的评论replys（即回复，一个List集合）
            4.评论的回复数量replyCount
             */
            Map<String, Object> commentVo = new HashMap<>();
//            1.评论
            commentVo.put("comment", comment);
//            2.评论的作者
            commentVo.put("user", userService.findUserById(comment.getUserId()));
//            加更1：每个帖子都要显示它的准确点赞数和当前用户的点赞状态
            likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
            likeStatus = hostHolder.getUser() == null ? 0 :
                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
            commentVo.put("likeCount", likeCount);
            commentVo.put("likeStatus", likeStatus);

//            评论的回复也要添加，找出该评论所有回复，回复由于不多，就不需要分页显示了
            List<Comment> replyList = commentService.findCommentsByEntity(
                    ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
            List<Map<String, Object>> replyVoList = new ArrayList<>();
            for (Comment reply : replyList) {
                /*
                每个reply包括三个元素，通过
                1.回复实体类reply
                2.回复的作者user
                3.回复对象target（可能没有）
                 */
                Map<String, Object> replyVo = new HashMap<>();
                replyVo.put("reply", reply);
                replyVo.put("user", userService.findUserById(reply.getUserId()));

//                加更1：显示每个回复的点赞数量和当前用户的点赞状态
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                replyVo.put("likeCount", likeCount);
                replyVo.put("likeStatus", likeStatus);

//                注意：回复存在指定用户回复的情况，所以需要特别检查target_id
                User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                replyVo.put("target", target);
//                最后加入到当前评论的回复集合中
                replyVoList.add(replyVo);
            }
//            3.评论的评论（list集合）
            commentVo.put("replys", replyVoList);
            int replyCount = commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
//            4.评论的回复数量
            commentVo.put("replyCount", replyCount);

//            最后将每个评论Map添加到最后的list集合中，用于返回给Model
            commentVoList.add(commentVo);
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
