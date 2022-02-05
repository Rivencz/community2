package com.nowcoder.community2.controller;

import com.nowcoder.community2.annotation.LoginRequired;
import com.nowcoder.community2.entity.DiscussPost;
import com.nowcoder.community2.entity.User;
import com.nowcoder.community2.service.DiscussPostService;
import com.nowcoder.community2.service.UserService;
import com.nowcoder.community2.util.CommunityUtil;
import com.nowcoder.community2.util.HostHolder;
import com.nowcoder.community2.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Controller
//添加一个访问路径
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 进入用户设置界面
     *
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传图片的逻辑
     *
     * @param headerImage 名字要和前端对应的name属性相同，这样可以直接将上传的图片属性映射到这里
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "传入图片为空！");
            return "/site/setting";
        }
//        获取传入的文件名
        String fileName = headerImage.getOriginalFilename();
//        只要文件类型，判断类型是否合法
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (suffix == null) {
            model.addAttribute("error", "传入的图片格式错误！");
            return "/site/setting";
        }
//        为了防止多个用户传入的图片名相同，所以我们生成一个随机字符串作为新的图片名字传入到服务器中
//        注意有一个点，只需要图片类型的时候不带这个点，加入路径的时候需要带
        fileName = CommunityUtil.generateUUID() + "." + suffix;
//        该图片上传到本地服务器的路径如下：
        File dest = new File(uploadPath + "/" + fileName);
        try {
//            将图片上传到本地服务器上
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败！" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }
//        上传完成，还需要修改一下web访问图片的路径，设置为以下路径来进行图片的访问
//        所以我们需要额外定义一个专门访问图片的方法
//        http://localhost:3306/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

//        最后重定向到首页
        return "redirect:/index";
    }

    /**
     * 用来从本地服务器返回图片信息
     *
     * @param filename 文件名
     * @param response 返回图片
     */
//    http://localhost:3306/community/user/header/xxx.png
    @RequestMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
//        将文件名变成本地服务器路径下的文件名
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix);

//        包在try的括号后面，系统会自动生成一个finally并将该括号内的流进行关闭
        try (
                OutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(filename);
        ) {
//            将本地服务器对应的文件以流的形式写入到response中
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败！" + e.getMessage());
        }
    }

}
