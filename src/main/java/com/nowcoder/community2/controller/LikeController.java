package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.LikeService;
import com.nowcoder.community2.util.CommunityUtil;
import com.nowcoder.community2.util.HostHolder;
import com.nowcoder.community2.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId){
//        后续我们会统一管理权限
        User user = hostHolder.getUser();

        likeService.like(user.getId(), entityType, entityId, entityUserId);
        Map<String,Object> map = new HashMap<>();
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(0, null, map);
    }
}
