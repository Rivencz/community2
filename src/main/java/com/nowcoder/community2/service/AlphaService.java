package com.nowcoder.community2.service;

import com.nowcoder.community2.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//设置bean是否为单例，默认是singleton，也就是只会被实例化一次，之后就一直使用这一个
@Scope
public class AlphaService {

    AlphaService() {
//        System.out.println("实例化该类");
    }

    //    构造方法执行完执行
//    @PostConstruct
//    public void postConstruct() {
//        System.out.println("实例化之后的操作");
//    }

    //    实例销毁之前
//    @PreDestroy
//    public void destroy() {
//        System.out.println("销毁之前！再见啦！");
//    }

    @Autowired
//    指定使用哪一个
    @Qualifier("hibernate")
    private AlphaDao alphaDao;

    public String find() {
        return alphaDao.select();
    }
}
