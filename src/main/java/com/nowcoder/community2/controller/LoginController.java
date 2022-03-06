package com.nowcoder.community2.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.UserService;
import com.nowcoder.community2.util.CommunityConstant;
import com.nowcoder.community2.util.CommunityUtil;
import com.nowcoder.community2.util.CookieUtil;
import com.nowcoder.community2.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    public static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 访问注册界面
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * 访问登录界面
     *
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 处理注册逻辑，注册界面提交表单之后跳转到此处
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);

//        如果成功，会跳转到一个中转界面，最后跳转到首页，通过target设置
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，已经像您的邮箱发送了一个激活邮件，请尽快点击链接进行激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
//            有错误，继续返回到刚才的注册界面
            return "/site/register";
        }
    }

    //    邮件中的链接路径：localhost:8080/community/mail/activation/用户id/激活码
    @RequestMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号现在可以正常使用了！");
//            激活成功，就跳转到登录界面
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "该账号已经激活，请勿重复操作！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，激活码错误！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 用于返回生成的验证码，图片通过response返回到浏览器
     *
     * @param response
//     * @param session  由于验证码需要在用户提交表单之后再次使用，所以将它保存到session中供后续使用
     */
    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response /*HttpSession session*/) {

//        根据我们的配置随机生成一个字符串，并存入到session中
        String text = kaptchaProducer.createText();
//        生成一个标识当前准备登录用户的字符串
        String owner = CommunityUtil.generateUUID();
        String redisKey = RedisKeyUtil.getKaptcha(owner);
//        将验证码放入key中，设置过期时间一分钟
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);
//        同时需要将标识登录者的随机字符串放入Cookie中
        Cookie cookie = new Cookie("kaptchaOwner", owner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

//        session.setAttribute("kaptcha", text);

//        将该字符串转换成图片格式，并返回到客户端
        BufferedImage image = kaptchaProducer.createImage(text);
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    /**
     * 处理登录表单
     *
     * @param model
     * @param username   输入的用户名
     * @param password   输入的的密码
     * @param code       输入的验证码
     * @param rememberme 用户是否勾选记住我
     * @param response   用来返回一个cookie给浏览器
//     * @param session    用来获取存放在服务器中的验证码，和用户输入的进行对比
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(Model model, String username, String password, String code,
                        boolean rememberme, HttpServletResponse response, @CookieValue(value = "kaptchaOwner", required = false) String kaptchaOwner/*,HttpSession session*/) {
//        先判断验证码是否正确
//        String kaptcha = session.getAttribute("kaptcha").toString();
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            kaptcha = (String) redisTemplate.opsForValue().get(RedisKeyUtil.getKaptcha(kaptchaOwner));
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码错误或验证码已过期！");
            return "/site/login";
        }
//        在调用业务层代码进行业务处理之前，先根据是否勾选记住我来设置不同的过期时间
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
//        如果map中有ticket，说明登录成功，返回了一个登录凭证
        if (map.containsKey("ticket")) {
//            将这个凭证作为cookie传给浏览器，以后服务器识别浏览器就靠这个凭证
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
//            设置cookie有效路径和失效时间
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
//            否则，说明登录失败，重新返回到登陆界面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 退出功能
     *
     * @param ticket 根据登录凭证修改对应凭证的状态
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
//        重定向到登陆界面
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
