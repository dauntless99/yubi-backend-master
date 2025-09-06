package com.yupi.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private MyMessageProducer myMessageProducer;

    @Resource
    private BiMessageProducer biMessageProducer;

    @Resource
    private DelayMessageProducer delayMessageProducer;

    @Test
    void sendMessage() {
        System.out.println("开始发送消息");
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", "你好呀");
    }


    @Test
    void testNormalMessage() {
        // 发送业务消息（例如：BI生成任务）
        String message = "{\"userId\":1,\"chartId\":100,\"content\":\"test normal message\"}";
        biMessageProducer.sendMessage(message);
        System.out.println("正常消息发送完成");
    }


    @Test
    void testDelayMessage() {
        String message = "{\"userId\":2,\"chartId\":200,\"content\":\"test delay 5s\"}";
        // 延迟5秒发送
        delayMessageProducer.sendDelayMessage(message, 5000);
        System.out.println("延迟消息发送完成，当前时间：" + LocalDateTime.now());
    }
}