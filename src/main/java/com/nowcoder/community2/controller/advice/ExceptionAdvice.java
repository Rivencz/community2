package com.nowcoder.community2.controller.advice;

import com.nowcoder.community2.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//表示管理所有被Controller注解标注的类，出现问题就到这里
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 异常处理方法，注解后面的类表示处理的异常范围
     * @param e 出现的异常
     * @param request
     * @param response
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse  response) throws IOException {
        logger.error(e.getMessage());
//        循环打印所有错误信息
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
//        如果是异步请求出错，那么并不需要返回到某一个界面，所以要分情况讨论
        String header = request.getHeader("x-requested-with");
//        如果为true，那么就是异步请求格式，返回一个错误提示而不是一个界面
        if("XMLHttpRequest".equals(header)){
//            表示返回的是字符串
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        }else{
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }
}
