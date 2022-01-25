package com.nowcoder.community2.controller;

import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.UserService;
import com.nowcoder.community2.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {


    @Autowired
    UserService userService;

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
     * 注册界面提交表单之后跳转到该请求
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


}
