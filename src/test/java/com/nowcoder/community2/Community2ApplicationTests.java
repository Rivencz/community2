package com.nowcoder.community2;

import com.nowcoder.community2.config.AlphaConfig;
import com.nowcoder.community2.dao.AlphaDao;
import com.nowcoder.community2.dao.UserMapper;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class Community2ApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 初始化Spring容器，所有对象只要通过注解标志，就都放在了该容器中
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 测试bean的一些其他功能，如检查bean只会被实例化一次，可以在bean初始化之后，销毁之前做一些事情
     */
    @Test
    public void testBeanManagement(){
        AlphaService bean = applicationContext.getBean(AlphaService.class);
        System.out.println(bean);
    }

    @Test
    public void testConfig(){
//        获取自定义的配置类
        SimpleDateFormat bean = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(bean.format(new Date()));
    }

    @Test
    void testApplicationContext() {
//        证明它是存在的
        System.out.println(applicationContext);

//        测试通过bean容器拿到我们自定义的对象，注意该对象必须注入到容器中
//        如果有多个实现类，就不能直接通过接口查找了，会报错
//        1.要么在实现类上加一个注解Primary来提高它的优先级
//        2.要么可以在Repository上自定义类名，来通过这个名字直接访问
        AlphaDao bean = applicationContext.getBean(AlphaDao.class);
        bean.select();
        AlphaDao hibernate = applicationContext.getBean("hibernate", AlphaDao.class);
        hibernate.select();
    }


    @Autowired
    UserMapper userMapper;

    @Test
    void contextLoads() {
        User user = userMapper.selectById(13);
        System.out.println(user);
    }

}
