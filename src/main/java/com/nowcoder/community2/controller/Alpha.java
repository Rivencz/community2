package com.nowcoder.community2.controller;

import com.nowcoder.community2.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class Alpha {

    // ResponseBody用来将返回值当作字符串看待而非一个网页路径
    @ResponseBody
    @RequestMapping("/hello")
    public String test(){
        return "Hello World";
    }

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @ResponseBody
    @RequestMapping("/students")
    public String getStudents(
//            通过注解对参数加以声明，如参数名，只要请求路径中有就会一一对应，如是否必须赋值，如没有赋值默认值为多少等等
            @RequestParam(name = "hello", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "many students";
    }

//    第二种方式，将参数直接写入路径中，通过PathVariable注解进行解释
    @ResponseBody
    @RequestMapping("/student/{id}")
    public String getStudent(
            @PathVariable(name = "id") int id){
        System.out.println(id);
        return "a student";
    }

//    通过表单获取参数
    @ResponseBody
    @RequestMapping("/student")
    public String getStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

//    返回到一个视图界面
    @RequestMapping("/getTeacher")
//    Model或者ModelAndView会自动帮我们实例化
    public String toView(Model model){
        model.addAttribute("name", "Lisa");
        model.addAttribute("age", "22");
//        注意：如果要返回到template目录下的界面，不需要后缀名，默认会自动加上，所以实际上访问的还是view.html
        return "/demo/view";
    }

    @GetMapping("/emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "riven");
        map1.put("age", 30);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "fiora");
        map2.put("age", 50);
        list.add(map1);
        list.add(map2);
        return list;
    }
}
