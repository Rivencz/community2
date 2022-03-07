package com.nowcoder.community2.config;

import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
//    三个configure方法，分别用来过滤静态资源、认证、授权
//    认证我们通过自己的登录拦截器实现，不用它的了，但是注意返回的东西我们要自己定义好

    @Override
    public void configure(WebSecurity web) throws Exception {
//        过滤掉全部静态资源
        web.ignoring().antMatchers("/resources/**");
    }

    //    授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        对特定路径进行特定权限的限制
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                ).hasAnyAuthority(
//                        只有用户，管理员，版主才能访问以上路径
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                ).antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                ).hasAnyAuthority(
//                        只有版主才能点赞和加精
                        AUTHORITY_MODERATOR
                ).antMatchers(
                        "/discuss/delete",
                        "/data/**"
                ).hasAnyAuthority(
//                        只有管理员才能删除和查看网站数据
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
//                关闭csrf防御
                .and().csrf().disable();

//        如果权限不够的处理操作
        http.exceptionHandling()
//                没有登录的操作
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//                        因为请求有异步请求和普通请求，异步请求需要返回一句话而不是一个html界面，所以要分情况写
                        String header = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(header)) {
//                            如果是异步请求，返回一句话
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你还没有登录，请先登录~"));
                        } else {
//                            否则，返回到登录界面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
//                登录了但是权限不够的操作
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String header = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(header)) {
//                            如果是异步请求，返回一句话
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限！"));
                        } else {
//                            否则，返回到错误界面
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

//        logout自动拦截，但是我们自己定义了退出操作，所以我们需要覆盖security的退出逻辑，只需要改变它的拦截路径，让他拦截到一个不存在的即可
        http.logout().logoutUrl("/securitylogout");
    }
}
