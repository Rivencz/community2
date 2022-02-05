package com.nowcoder.community2.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    /**
     * 将服务器的JSON对象转换成JSON字符串返回给浏览器，这样浏览器就可以识别
     * @param code 编码，数字不同表示的意义也不同
     * @param msg 返回的提示信息
     * @param map 业务信息，不同的业务内容也不同，因此使用Map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (map != null) {
            for(String key : map.keySet()){
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

//    对以上方法进行一个重载
    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "riven");
        map.put("age", 22);
//        {"msg":"sendOk","code":0,"name":"riven","age":22}
        System.out.println(getJSONString(0, "sendOk", map));

    }
}
