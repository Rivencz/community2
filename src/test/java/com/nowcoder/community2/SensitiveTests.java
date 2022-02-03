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
        String text = "哈哈，我超爱赌博嫖娼以及吸毒！哈哈我真的很感恩这个世界riven，我和";

        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
