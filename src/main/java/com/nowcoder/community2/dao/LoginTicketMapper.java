package com.nowcoder.community2.dao;

import com.nowcoder.community2.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LoginTicketMapper {

//    该接口通过注解的方式来实现对应方法

    /**
     * 插入一个登录凭证
     * @param loginTicket
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 根据浏览器传递的登录凭证来查询该登录凭证是否存在
     * @param ticket
     * @return
     */
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket ",
            "where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 修改登录凭证状态
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            "update login_ticket ",
            "set status=#{status} ",
            "where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);
}
