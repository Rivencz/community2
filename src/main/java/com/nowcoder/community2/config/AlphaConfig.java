package com.nowcoder.community2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

//自定义配置类
@Configuration
public class AlphaConfig {

//    装配到容器中
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
    }
}
