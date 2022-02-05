package com.nowcoder.community2;


import com.nowcoder.community2.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void test1(){
        String text = "哈哈，你是不是想赌。博和嫖-娼以及吸*毒！哈哈真的很感x恩这个世界";

        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
