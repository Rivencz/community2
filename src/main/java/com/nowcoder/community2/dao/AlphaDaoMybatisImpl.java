package com.nowcoder.community2.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
//提高该实现类的优先级
@Primary
public class AlphaDaoMybatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "Im Mybatis";
    }
}
