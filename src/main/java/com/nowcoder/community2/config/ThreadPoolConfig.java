package com.nowcoder.community2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//开启定时任务线程所必须的配置类
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {

}
