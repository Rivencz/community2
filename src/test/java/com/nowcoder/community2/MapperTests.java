package com.nowcoder.community2;

import com.nowcoder.community2.dao.DiscussPostMapper;
import com.nowcoder.community2.dao.LoginTicketMapper;
import com.nowcoder.community2.dao.UserMapper;
import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.LoginTicket;
import com.nowcoder.community2.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Test
    public void selectTest(){
        User user = userMapper.selectById(13);
        System.out.println(user);
        user = userMapper.selectByName("guanyu");
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder25@sina.com");
        System.out.println(user);
    }

    @Test
    public void insertTest(){
        User test = new User();
        test.setUsername("test2");
        test.setPassword("1234222");
        test.setEmail("test2@qq.com");
        test.setActivationCode("123344522");
        test.setHeaderUrl("http://images.nowcoder.com/head/123t.png");
        int rows = userMapper.insertUser(test);
        System.out.println(rows);
        User test1 = userMapper.selectByName("test2");
        System.out.println(test1);
    }

    @Test
    public void updateTest(){
        int i = userMapper.updateStatus(151, 1);
        System.out.println(i);
        int i1 = userMapper.updateHeader(151, "http://images.nowcoder.com/head/149t.png");
        System.out.println(i1);
        int i2 = userMapper.updatePassword(151, "54321");
        System.out.println(i2);
    }

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void selectDiscussPostsTest(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost post : discussPosts){
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void insertDiscussPost(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(149);
        discussPost.setTitle("求职问答贴2");
        discussPost.setContent("知无不言言无不尽");
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setScore(0);
        int i = discussPostMapper.insertDiscussPost(discussPost);
        System.out.println(i);
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }
    }

    @Test
    public void insertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(102);
        loginTicket.setTicket("123421314543");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);


    }

    @Test
    public void selectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("1234321");
        System.out.println(loginTicket);

        int i = loginTicketMapper.updateStatus("1234321", 1);
        System.out.println(i);

        System.out.println(loginTicketMapper.selectByTicket("1234321"));

    }

}
