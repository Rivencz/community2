package com.nowcoder.community2.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community2.entity.Message;
import com.nowcoder.community2.entity.Page;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.MessageService;
import com.nowcoder.community2.service.UserService;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.CommunityUtil;
import com.nowcoder.community2.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //查看私信列表
    @RequestMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
//        首先要找出来当前登录的用户
        User user = hostHolder.getUser();
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setPath("/letter/list");
        page.setLimit(5);

        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
//        将需要的信息存放在以下list集合中
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message conversation : conversationList) {
            /*
            每个会话map需要包含以下信息
            1.会话信息conversation
            2.未读message数量
            3.目标用户（获取它的头像
            4.该会话所有的message数量（用于显示
             */
                Map<String, Object> map = new HashMap<>();
//              1.当前会话
                map.put("conversation", conversation);
                int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId());
//              2.未读私信数量
                map.put("unreadCount", letterUnreadCount);
//              3.目标用户
                int targetId = conversation.getFromId() == user.getId() ? conversation.getToId() : conversation.getFromId();
                map.put("target", userService.findUserById(targetId));
//              4.会话所有message数量
                int letterCount = messageService.findLetterCount(conversation.getConversationId());
                map.put("letterCount", letterCount);
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
//        查询该用户所有会话和通知中message的未读数量
        model.addAttribute("letterUnreadCount", messageService.findLetterUnreadCount(user.getId(), null));
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "site/letter";
    }

    @RequestMapping("/letter/detail/{conversationId}")
    public String letterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letter/detail/" + conversationId);

        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                /*
                每个message包括以下几个信息
                1.message实体
                2.fromUser也就是发信人
                 */
                Map<String, Object> map = new HashMap<>();
//                1.message实体
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));

//        显示完详情，还需要将当前会话的所有信息都标记为已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "site/letter-detail";
    }

    /**
     * 找出一个会话中所有未读的Message对应的id
     * 注意：要保证toId是当前登录用户，不能一股脑找全部未读的信息
     *
     * @param letterList 传入一个详细Message列表
     * @return
     */
    public List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 用于私信详情中找到目标用户
     *
     * @param conversationId
     * @return
     */
    public User getLetterTarget(String conversationId) {
        String[] s = conversationId.split("_");
        int s0 = Integer.parseInt(s[0]);
        int s1 = Integer.parseInt(s[1]);
        return hostHolder.getUser().getId() == s0 ? userService.findUserById(s1) : userService.findUserById(s0);
    }

    /**
     * 发送私信
     *
     * @param toName  发送目标的用户名
     * @param content 发送的内容
     * @return
     */
    @RequestMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
//        首先根据用户名找到对应的用户User
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
//        保证两个用户的会话对应的会话id中永远小的在前，大的在后进行拼接形成最后的会话Id
        if (message.getToId() < message.getFromId()) {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        } else {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(value = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空!");
        }
//        评论类通知
        Map<String, Object> messageVO = new HashMap<>();
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        if (message != null) {
            messageVO.put("message", message);
//            通知的内容,需要先进行还原,然后放入到map集合中
            String content = HtmlUtils.htmlUnescape(message.getContent());
//            将JSON格式内容转换成它的原类型,HashMap
            HashMap data = JSONObject.parseObject(content, HashMap.class);
//            事件的发起者
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
//            对于点赞和评论,还有一个额外的postId
            messageVO.put("postId", data.get("postId"));

            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
//            通知和未读信息数量
            messageVO.put("count", noticeCount);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", noticeUnreadCount);
            model.addAttribute("commentNotice", messageVO);
        }

//        点赞类通知
        messageVO = new HashMap<>();
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        if (message != null) {
            messageVO.put("message", message);
//            通知的内容,需要先进行还原,然后放入到map集合中
            String content = HtmlUtils.htmlUnescape(message.getContent());
//            将JSON格式内容转换成它的原类型,HashMap
            HashMap data = JSONObject.parseObject(content, HashMap.class);
//            事件的发起方
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
//            对于点赞和评论,还有一个额外的postId
            messageVO.put("postId", data.get("postId"));

            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
//            通知和未读信息数量
            messageVO.put("count", noticeCount);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", noticeUnreadCount);
            model.addAttribute("likeNotice", messageVO);
        }

//        关注类通知
        messageVO = new HashMap<>();
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if (message != null) {
            messageVO.put("message", message);
//            通知的内容,需要先进行还原,然后放入到map集合中
            String content = HtmlUtils.htmlUnescape(message.getContent());
//            将JSON格式内容转换成它的原类型,HashMap
            HashMap data = JSONObject.parseObject(content, HashMap.class);
//            事件的发起方
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
//            通知和未读信息数量
            messageVO.put("count", noticeCount);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", noticeUnreadCount);
            model.addAttribute("followNotice", messageVO);
        }

//        查询所有的未读私信和通知数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "site/notice";
    }

    @RequestMapping(value = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
//        指定用户指定通知的总数
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        page.setPath("/notice/detail/" + topic);

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if(noticeList != null){
            for(Message notice : noticeList){
                Map<String, Object> map = new HashMap<>();
//                通知
                map.put("notice", notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
//                内容
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
//                通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

//        将所有未读通知标记为已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return"site/notice-detail";
    }
}
