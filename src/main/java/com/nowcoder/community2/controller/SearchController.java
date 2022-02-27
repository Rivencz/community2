package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.Page;
import com.nowcoder.community2.service.ElasticsearchService;
import com.nowcoder.community2.service.LikeService;
import com.nowcoder.community2.service.UserService;
import com.nowcoder.community2.util.CommunityConstant;
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
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    /**
     * 根据前端传入的搜索框内容进行搜索
     * @param keyWord
     * @param page
     * @param model
     */
//    /search?keyWord=xxx
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(String keyWord, Page page, Model model){

        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.search(keyWord, page.getCurrent() - 1, page.getLimit());

//        搜索出来的帖子通过列表传入model
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchResult != null){
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
//                帖子实体
                map.put("post", post);
//                帖子对应的用户
                map.put("user", userService.findUserById(post.getUserId()));
//                帖子点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
//        保证搜索之后搜索框中内容一直存在
        model.addAttribute("keyWord", keyWord);

//        分页相关设置
        page.setPath("/search?keyWord=" + keyWord);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }

}
