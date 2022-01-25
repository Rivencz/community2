package com.nowcoder.community2;

import com.nowcoder.community2.util.MailClient;
import org.apache.logging.log4j.util.StringBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void testSend(){
        mailClient.sendMail("fiorac@163.com", "测试", "我服了！！");
    }

    @Test
    public void testSendHTML(){
        Context context = new Context();
        context.setVariable("title", "欢迎来到召唤师峡谷！");
        context.setVariable("username", "放逐之刃");
//        通过模板引擎执行demo.html界面，之后生成字符串文件，但本质上是一个HTML格式文件
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("fiorac@163.com", "最后一！223232！！！", content);
    }
}

