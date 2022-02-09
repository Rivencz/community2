package com.nowcoder.community2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowcoder.community2.dao.AlphaDao;
import com.nowcoder.community2.dao.DiscussPostMapper;
import com.nowcoder.community2.dao.UserMapper;
import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

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

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    TransactionTemplate transactionTemplate;

    public String find() {
        return alphaDao.select();
    }

    /**
     * 测试事务操作，添加用户的同时会发送一个帖子，制造一个错误查看是否可以回滚
     * @return
     */
    //propagation表示传播方式，当当前事务中调用了外部业务的时候，外务业务的事务机制如果使用
    //REQUIRED是默认的，表示需要事务，如果外部调用有就用它的，如果没有我就创建一个新的
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("1234@qq.com");
        user.setCreateTime(new Date());

        System.out.println(userMapper.insertUser(user));

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("欢迎新人！");
        post.setTitle("你好啊新人，欢迎来到德莱联盟！");
        post.setCreateTime(new Date());
        System.out.println(discussPostMapper.insertDiscussPost(post));

//        制造一个错误！
        Integer.valueOf("abc");
        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                User user = new User();
                user.setUsername("alpha");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("1234@qq.com");
                user.setCreateTime(new Date());

                System.out.println(userMapper.insertUser(user));

                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("欢迎新人！");
                post.setTitle("你好啊新人，欢迎来到德莱联盟！");
                post.setCreateTime(new Date());
                System.out.println(discussPostMapper.insertDiscussPost(post));

//        制造一个错误！
                Integer.valueOf("abc");

                return "ok";
            }
        });
    }
}
