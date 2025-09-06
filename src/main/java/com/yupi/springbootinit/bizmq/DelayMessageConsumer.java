// src/main/java/com/yupi/springbootinit/bizmq/
// 新增 DelayMessageConsumer.java
package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * 延迟消息消费者
 */
@Component
@Slf4j
public class DelayMessageConsumer {

    @Resource
    private BiMessageProducer biMessageProducer;

    /**
     * 处理重试队列消息
     */
    @SneakyThrows
    @RabbitListener(queues = {MqConstant.RETRY_QUEUE_NAME}, ackMode = "MANUAL")
    public void handleRetryMessage(String message,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                   @Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        log.info("处理重试消息: {}", message);
        try {
            // 尝试重新处理消息（这里转发到原有业务队列）
            biMessageProducer.sendMessage(message);
            channel.basicAck(deliveryTag, false);
            sendTraceMessage(messageId, "RETRY_SUCCESS", message);
        } catch (Exception e) {
            log.error("重试消息处理失败", e);
            // 拒绝消息并进入死信队列
            channel.basicNack(deliveryTag, false, false);
            sendTraceMessage(messageId, "RETRY_FAILED", e.getMessage());
        }
    }

    /**
     * 消息追踪消费者
     */
    @RabbitListener(queues = {MqConstant.TRACE_QUEUE_NAME})
    public void handleTraceMessage(String message) {
        log.info("消息追踪: {}", message);
        // 实际应用中可以存储到数据库或日志系统
    }

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

    @Resource
    private RabbitTemplate rabbitTemplate;
}