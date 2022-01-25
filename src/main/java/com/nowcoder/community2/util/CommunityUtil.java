package com.nowcoder.community2.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    /**
     * 通过UUID生成随机字符串，并转换成没有特殊字符的字符串
     * @return
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 对传入的key进行MD5加密
     * @param key
     * @return
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
