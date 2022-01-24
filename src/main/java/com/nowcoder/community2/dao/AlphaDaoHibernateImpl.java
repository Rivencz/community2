package com.nowcoder.community2.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

//注入到Spring容器中
@Repository("hibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {

    @Override
    public String select() {
        return "Im Hibernate";
    }
}
