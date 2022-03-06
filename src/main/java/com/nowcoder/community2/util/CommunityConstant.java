package com.nowcoder.community2.util;

public interface CommunityConstant {

//    激活一个用户对应的三种状态码
    int ACTIVATION_SUCCESS = 0;

    int ACTIVATION_REPEAT = 1;

    int ACTIVATION_FAILURE = 2;

    /**
     * 默认过期时间12h
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选记住我的过期时间为1年
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 365;

    /**
     * Comment实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;


    /**
     * Comment实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * Comment实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：帖子
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：发布帖子
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 主题：删除帖子
     */
    String TOPIC_DELETE = "delete";

    /**
     * 系统用户
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 用户权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 用户权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 用户权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}
