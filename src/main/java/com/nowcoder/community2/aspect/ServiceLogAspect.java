package com.nowcoder.community2.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //    定义切入点，为所有业务方法
    @Pointcut("execution(* com.nowcoder.community2.service.*.*(..))")
    public void pointcut() {
    }

    //    定义切入方法，每次调用之前，通过日志进行记录
//    格式为：用户[x.x.x.x]在[xxx]访问了[com.nowcoder.community2.service.xxx()]
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        由于消费者接收消息没有通过controller层而直接调用了service层方法，所以该值为null
        if(attributes == null){
            return;
        }
//        获取到了request对象，
        HttpServletRequest request = attributes.getRequest();
//        通过request获取到用户的ip地址
        String ip = request.getRemoteHost();
//        时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                                                  获取类名                                            获取方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        logger.info(String.format("用户[%s]在[%s]访问了[%s]", ip, now, target));
    }
}
