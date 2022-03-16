package com.nowcoder.community2.quartz;


import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.service.DiscussPostService;
import com.nowcoder.community2.service.ElasticsearchService;
import com.nowcoder.community2.service.LikeService;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    //    牛客纪元常量，后续计算权重的时候需要使用
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败！", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子");
            return;
        }
        logger.info("[任务开始] 正在刷新帖子分数：" + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕");

    }

    public void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null || post.getStatus() == 2) {
            logger.error("帖子不存在或已经被删除！");
            return;
        }
//        计算权重
        boolean wonderful = post.getStatus() == 1;
        int commentCount = post.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;

//        计算最后分数 = 权重取对数 + 距离天数（创建时间和牛客纪元的天数差）
        double score = Math.log10(Math.max(w, 1)) +
                (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 60 * 60 * 24);
//        更新帖子和es数据信息
        discussPostService.updateScore(postId, score);
        elasticsearchService.saveDiscussPost(post);
    }


}
