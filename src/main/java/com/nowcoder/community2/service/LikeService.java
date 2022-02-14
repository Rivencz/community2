package com.nowcoder.community2.service;

import com.nowcoder.community2.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    RedisTemplate redisTemplate;

//    点赞相关的三个业务
    /*
    1.点赞
    2.返回点赞数量
    3.返回点赞状态
     */

    /**
     * 当前用户对实体进行点赞，分两种情况
     * 1.如果没有点赞，那么就点赞
     * 2.如果已经点赞，那么本次操作会取消赞
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember) {
////            如果已经点过了，取消赞
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        } else {
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }

//        重构
//        点赞的同时，要进行两步redis操作:
//        1.帖子/评论/回复对应key进行修改
//        2.点赞实体的userId对应key进行修改
//        所以需要在事务中进行编写
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
//                开启事务之前，先查询点赞状态，否则在事务中无法及时查询到数据，因为是最后统一执行的
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//                开启事务
                operations.multi();
                if (isMember) {
//                    如果已经点赞过了，就取消点赞
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                } else {
//                    否则，点赞
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
//                执行事务
                return operations.exec();
            }
        });
    }

    /**
     * 获取某一个实体的点赞数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 获取当前登录用户对实体的点赞状态
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        return isMember ? 1 : 0;
    }

    /**
     * 查询某个用户获得的赞
     *
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer likeCount = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return likeCount == null ? 0 : likeCount;
    }
}
