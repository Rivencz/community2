package com.nowcoder.community2.util;

import com.nowcoder.community2.entity.User;
import org.springframework.stereotype.Component;

/**
 * 保存用户信息，用来当作session使用
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
