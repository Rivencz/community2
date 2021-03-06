package com.nowcoder.community2.service;

import com.nowcoder.community2.dao.MessageMapper;
import com.nowcoder.community2.entity.Message;
import com.nowcoder.community2.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {


    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**查询当前用户的所有会话，每个会话取最后一次的message，该sql语句有点繁琐
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * 查询未读私信数量
     * @param userId 必须有
     * @param conversationId 可有可无，如果为空就是查询当前用户所有未读message
     *                       如果不为空，就是查看当前用户某一个指定会话的未读message
     * @return
     */
    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int addMessage(Message message){
        if(message == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));

        return messageMapper.insertMessage(message);
    }

    /**
     * 点击会话详情后，将未读Message的status进行修改，变为已读
     * @param ids
     * @return
     */
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids, 1);
    }

    /**
     * 查询最近的一条通知
     * @param userId 用户
     * @param topic 主题
     * @return
     */
    public Message findLatestNotice(int userId, String topic){
        return messageMapper.selectLatestNotice(userId, topic);
    }

    /**
     * 查询指定用户指定主题的通知数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    /**
     * 查询指定用户(指定主题)的未读通知数量
     * @param userId
     * @param topic 可选可不选
     * @return
     */
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    /**
     * 分页查询指定用户指定主题的部分通知
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findNotices(int userId, String topic, int offset, int limit){
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
