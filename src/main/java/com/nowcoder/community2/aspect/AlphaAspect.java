package com.nowcoder.community2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
//    主要编写两个内容1.切入点 2.通知

//    该注解表示切入点位置：后面的表达式意思为：service包下所有类的所有方法的所有参数都包括在内
    @Pointcut("execution(* com.nowcoder.community2.service.*.*(..))")
    public void pointcut(){
    }

//    2.通知
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
//        相当于执行了切入的目标对象的方法，在它的前后可以输入自己想要的内容
        Object proceed = joinPoint.proceed();
        System.out.println("around after");
        return proceed;
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterthrowing");
    }
}
