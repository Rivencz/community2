package com.nowcoder.community2.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
//        图片宽度
        properties.setProperty("kaptcha.image.width", "100");
//        高度
        properties.setProperty("kaptcha.image.height", "40");
//        文字大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");
//        颜色
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
//        用以产生随机字符的字符串
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
//        随机字符的个数
        properties.setProperty("kaptcha.textproducer.char.length", "4");
//        是否在图片上加上一些针对机器人破解的干扰
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

//        将我们设置的属性配置到kaptcha中，就依赖这个进行输出的
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);

        return kaptcha;
    }
}
