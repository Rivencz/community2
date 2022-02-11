package com.nowcoder.community2.dao;

import com.nowcoder.community2.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {

    //    查询指定用户和所有人的会话信息，分页显示，在简介页面上只显示他们之间会话的最后一条信息
    List<Message> selectConversations(int userId, int offset, int limit);

    //    查询会话数量
    int selectConversationCount(int userId);

    //    查看指定会话的详细信息，也是分页显示
    List<Message> selectLetters(String conversationId, int offset, int limit);

    int selectLetterCount(String conversationId);

    //    查看未读信息数量，第二个参数可有可无
    //      如果没有就是查看当前用户所有的
    //      如果有就是查看当前用户指定会话所有的
    int selectLetterUnreadCount(int userId, String conversationId);

//    插入一个Message实体
    int insertMessage(Message message);

    //    List集合中的每个数都代表一个Message实体类的id，将这些id所对应的status进行修改
    int updateStatus(List<Integer> ids, int status);
}
