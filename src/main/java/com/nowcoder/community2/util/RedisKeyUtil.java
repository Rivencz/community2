package com.nowcoder.community2.util;


public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 根据实体类型和id拼接出点赞所使用的key
     * like:entity:entityType:entityId -> set类型，存放点赞的用户id
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取用户点赞key
     * like:user:userId -> int类型，存放每个用户的获赞数量
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
