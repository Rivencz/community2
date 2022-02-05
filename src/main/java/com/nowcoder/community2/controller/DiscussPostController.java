package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.DiscussPostService;
import com.nowcoder.community2.service.UserService;
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

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403, "请先登录！");
        }
//        这个是自己写的异常处理，后续会统一处理异常
        if(StringUtils.isBlank(title) || StringUtils.isBlank(content)){
            return CommunityUtil.getJSONString(403, "标题或内容不能为空!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String findDiscussPostById(@PathVariable("discussPostId") int discussPostId, Model model){

        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(discussPost.getUserId());
//        把帖子和用户都传给前端界面进行使用
        model.addAttribute("post", discussPost);
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}
