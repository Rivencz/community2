package com.nowcoder.community2.config;

import com.nowcoder.community2.controller.interceptor.AlphaInterceptor;
import com.nowcoder.community2.controller.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community2.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    AlphaInterceptor alphaInterceptor;

    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        注意：拦截器的拦截顺序和以下注册顺序相同！

//        添加测试使用的拦截器，并配置拦截的路径，过滤静态界面
        registry.addInterceptor(alphaInterceptor)
                .addPathPatterns("/login", "/register")
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
//        添加登录凭证拦截器，拦截所有路径，主要用于首页顶部信息的展示
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
//        拦截静态界面
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

    }

}
