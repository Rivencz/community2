package com.nowcoder.community2;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoggerTests {
    Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger(){
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("info message");
        logger.error("info message");


    }
}
