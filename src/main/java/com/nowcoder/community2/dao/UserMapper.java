package com.nowcoder.community2.dao;

import com.nowcoder.community2.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

//注入到容器中
@Repository
//标记该接口是一个Mybatis的Mapper（没这个Mapper就报错）
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String name);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

}
