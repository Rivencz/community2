package com.nowcoder.community2.service;

import com.nowcoder.community2.dao.UserMapper;
import com.nowcoder.community2.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }
}
