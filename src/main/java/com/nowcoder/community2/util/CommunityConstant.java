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
}
