package com.nowcoder.community2.controller.interceptor;

import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.MessageService;
import com.nowcoder.community2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

//    模板引擎渲染之前，因为在controller之后有ModelAndView变量，可以向前端带一些数据
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
//            将全部未读信息数量显示在头部消息框
            modelAndView.addObject("allUnreadCount", noticeUnreadCount + letterUnreadCount);
        }
    }
}
