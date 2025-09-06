// src/main/java/com/yupi/springbootinit/bizmq/
// 新增 DelayMessageProducer.java
package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.UUID;

/**
 * 延迟消息生产者
 */
@Component
public class DelayMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送延迟消息
     * @param message 消息内容
     * @param delayMillis 延迟时间(毫秒)
     */
    public void sendDelayMessage(String message, long delayMillis) {
        // 生成消息ID用于追踪
        String messageId = UUID.randomUUID().toString();

        // 构建消息
        Message<String> msg = MessageBuilder
                .withPayload(message)
                .setHeader(AmqpHeaders.MESSAGE_ID, messageId)
                .setHeader(AmqpHeaders.DELIVERY_MODE, 2) // 持久化消息
                .setHeader("delay", delayMillis)
                .build();

        // 发送到延迟队列
        rabbitTemplate.convertAndSend(
                MqConstant.DELAY_EXCHANGE_NAME,
                MqConstant.DELAY_ROUTING_KEY,
                msg,
                postProcessor -> {
                    postProcessor.getMessageProperties().setExpiration(String.valueOf(delayMillis));
                    return postProcessor;
                }
        );

        // 发送追踪消息
        sendTraceMessage(messageId, "SEND", message);
    }

    /**
     * 发送消息追踪日志
     */
    private void sendTraceMessage(String messageId, String status, String content) {
        String traceMsg = String.format(
                "{\"messageId\":\"%s\",\"status\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                messageId, status, content, System.currentTimeMillis()
        );
        rabbitTemplate.convertAndSend(
                MqConstant.TRACE_EXCHANGE_NAME,
                MqConstant.TRACE_ROUTING_KEY,
                traceMsg
        );
    }
}