package com.nowcoder.community2;

import com.nowcoder.community2.dao.DiscussPostMapper;
import com.nowcoder.community2.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class DiscussPostTests {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void insertTest(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(149);
        discussPost.setTitle("求职问答贴");
        discussPost.setContent("大家有什么问题都可以问");
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setScore(0);
        int i = discussPostMapper.insertDiscussPost(discussPost);
        System.out.println(i);
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);

    }
}
