package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.Page;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.DiscussPostService;
import com.nowcoder.community2.service.LikeService;
import com.nowcoder.community2.service.UserService;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
//        SpringMVC会自动帮我们实例化model和page，会自动将page添加到model中，所以不需要手动进行这一步
//        设置一下page中有两个需要服务器进行设置的参数，剩下两个是浏览器传入进来的
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<Map<String, Object>> discussPosts = new ArrayList<>();
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        if (list != null) {
            for (DiscussPost discussPost : list) {
//            将每一个帖子集合和对应的user都封装到一个map中，这样显示了帖子同时也能显示他的用户名
                Map<String, Object> map = new HashMap<>();
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                map.put("post", discussPost);
//                后续加更1：为了保证帖子点赞数量显示正确，需要记录每个帖子的点赞数量，显示到主页上（此处不需要记录点赞状态）
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

//    权限不足时返回的界面
    @RequestMapping(value = "/denied", method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }
}

