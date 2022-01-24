package com.nowcoder.community2;

import com.nowcoder.community2.dao.DiscussPostMapper;
import com.nowcoder.community2.dao.UserMapper;
import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    UserMapper userMapper;


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
}
