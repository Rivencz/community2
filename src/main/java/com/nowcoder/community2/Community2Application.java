package com.nowcoder.community2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Community2Application {

//    解决netty冲突
    @PostConstruct
    public void init(){
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(Community2Application.class, args);
    }

}
