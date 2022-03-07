package com.nowcoder.community2.controller;

import com.nowcoder.community2.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    //    跳转到data页面，只有管理员能进行跳转
//    由于后续两个获取UV和DAU的操作是POST请求，并且会跳转到该页面，所以该方法必须还要支持POST请求，否则跳转会失败
    @RequestMapping(value = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {

        return "/site/admin/data";
    }

    @RequestMapping(value = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long uvResult = dataService.calculateUV(start, end);

        model.addAttribute("uvStart", start);
        model.addAttribute("uvEnd", end);
        model.addAttribute("uvResult", uvResult);

//        通过请求转发的方式跳转到统计界面，这样可以确保在同一个请求中，传入的model中的参数可以使用
        return "forward:/data";
    }

    @RequestMapping(value = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dauResult = dataService.calculateDAU(start, end);

        model.addAttribute("dauStart", start);
        model.addAttribute("dauEnd", end);
        model.addAttribute("dauResult", dauResult);

        return "forward:/data";
    }
}
