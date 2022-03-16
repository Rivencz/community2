package com.nowcoder.community2.util;


import java.util.Date;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    //    存放验证码的key
    private static final String PREFIX_KAPTCHA = "kaptcha";
    //    存放登录凭证的key
    private static final String PREFIX_TICKET = "ticket";
    //    存放用户实体信息
    private static final String PREFIX_USER = "user";
    //    存放UV和DAU的key
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    //    存放帖子key
    private static final String PREFIX_POST = "post";


    /**
     * 根据实体类型和id拼接出点赞所使用的key
     * like:entity:entityType:entityId -> set类型，存放点赞的用户id
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取用户点赞key
     * like:user:userId -> int类型，存放每个用户的获赞数量
     *
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 获取"我关注的"key
     * followee:userId,entityType -> zset(entityeId, now)
     *
     * @param userId
     * @param entityType 关注的实体类型（用户，帖子。。）
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 获取"关注我的"key
     * follower:entityType:entityId -> zset(userId, now)
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取验证码对应的key
     * kaptcha:一个标识用户的随机字符串 -> 登录验证码
     *
     * @param owner
     * @return
     */
    public static String getKaptcha(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 获取登录凭证对应的key，不再使用表login_ticket
     * ticket:一个随机字符串 -> 一个LoginTicket类型实体
     *
     * @param ticket
     * @return
     */
    public static String getTicket(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 存放用户实体
     *
     * @param userId
     * @return
     */
    public static String getUser(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    //    获取单日UV uv:time hyperloglog类型
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    //    获取日期区间的UV uv:start:end
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //    获取单日活跃 dau:time bitmap类型
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    //    获取日期区间活跃 dau:start:end
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    //    获取用来存放帖子id的key（set类型）
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }
}
