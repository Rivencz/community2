package com.nowcoder.community2.service;

import com.nowcoder.community2.dao.LoginTicketMapper;
import com.nowcoder.community2.dao.UserMapper;
import com.nowcoder.community2.entity.LoginTicket;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.CommunityUtil;
import com.nowcoder.community2.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    UserMapper userMapper;

    //发邮件需要的两个类
    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    //    访问激活码网址需要的链接地址
    @Value("${server.servlet.context-path}")
    String contextPath;

    @Value("${community.path.domain}")
    String domain;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    public User findUserById(int userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 开发注册功能
     *
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
//        首先对数据进行一个校验
//        如果用户为空，就出现了异常，直接报错，因为正常情况用户为空不可能走到这里
        if (user == null) {
            throw new IllegalArgumentException("参数不能位空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
//        都不为空了，就查看数据库中有没有已经存在相同的数据
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已经被注册！");
            return map;
        }

//        检验完成，数据都符合规范，那么就补充该用户的信息，准备调用dao层方法插入数据
//        生成一个随机五位字符串，用来构成密码
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
//        密码在传入之前进行了一次md5加密：将输入的密码和生成的随机数进行一个md5加密
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
//        设置头像地址，0-1000t.png都有图片
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

//        插入之后，发送一个激活邮件给用户，顺带上激活码
//        先通过模板引擎得到激活页面，然后将这个页面发送给用户对应邮箱
        Context context = new Context();
        context.setVariable("email", user.getEmail());
//        跳转到激活链接，这个链接是我们自己写的激活功能:
//        localhost:8080/community/mail/activation/用户id/激活码
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

//        通过模板引擎渲染激活界面，并将它发送给用户邮箱
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 激活用户业务
     *
     * @param userId 需要激活的用户id
     * @param code   传入的激活码
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
//            status为0，还要进行判断，看激活码是否正确，如果正确，修改status为1，否则，激活码错误
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
//            没激活，但是同时激活码错误，因此激活失败
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录业务
     *
     * @param username
     * @param password
     * @param expiredSeconds 登录凭证过期时间，注意类型为long，如果为int会造成溢出，从而出现错误！
     * @return
     */
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

//        首先查看用户是否存在
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
//        存在之后，查看该账户是否激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
//        用户可用，就检查密码是否正确
//        由于添加密码的时候我们进行过加密，所以需要加密之后再和数据库中用户的密码进行比较
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

//        将登录凭证传递给客户端，因为后续客户端进行操作需要使用这个ticket
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录业务，根据传入的凭证修改对应凭证的状态为1，表示失效
     *
     * @param ticket
     */
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 修改用户的头像路径
     * @param userId
     * @param headerUrl
     * @return
     */
    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }
}
