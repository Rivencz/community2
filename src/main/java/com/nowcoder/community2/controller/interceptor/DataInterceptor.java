package com.nowcoder.community2.controller.interceptor;

import com.nowcoder.community2.service.DataService;
import com.nowcoder.community2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    //    在执行controller之前，对所有路径进行拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        把当前访问服务器的ip放入UV中
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

//         如果当前有用户登录，那么就将他放入DAU中
        if (hostHolder.getUser() != null) {
            dataService.recordDAU(hostHolder.getUser().getId());
        }
        return true;
    }
}
