// src/test/java/com/yupi/springbootinit/bizmq/
// 新增 DelayMessageProducerTest.java
package com.yupi.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest
class DelayMessageProducerTest {

    @Resource
    private DelayMessageProducer delayMessageProducer;

    @Test
    void testSendDelayMessage() {
        // 发送延迟10秒的消息
        delayMessageProducer.sendDelayMessage("666", 10000);
    }

    @Test
    void testDelayMessage() {
        String message = "{\"userId\":2,\"chartId\":200,\"content\":\"test delay 5s\"}";
        // 延迟5秒发送
        delayMessageProducer.sendDelayMessage(message, 5000);
        System.out.println("延迟消息发送完成，当前时间：" + LocalDateTime.now());
    }
}