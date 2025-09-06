// src/main/java/com/yupi/springbootinit/bizmq/
// 新增 DelayInitMain.java
package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 初始化延迟队列
 */
public class DelayInitMain {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 声明延迟交换机
            channel.exchangeDeclare(MqConstant.DELAY_EXCHANGE_NAME, "direct");

            // 声明延迟队列（设置死信转发）
            Map<String, Object> delayArgs = new HashMap<>();
            // 绑定到重试交换机
            delayArgs.put("x-dead-letter-exchange", MqConstant.RETRY_EXCHANGE_NAME);
            delayArgs.put("x-dead-letter-routing-key", MqConstant.RETRY_ROUTING_KEY);
            // 默认延迟时间 30 秒
            delayArgs.put("x-message-ttl", 30000);

            channel.queueDeclare(MqConstant.DELAY_QUEUE_NAME, true, false, false, delayArgs);
            channel.queueBind(MqConstant.DELAY_QUEUE_NAME,
                    MqConstant.DELAY_EXCHANGE_NAME,
                    MqConstant.DELAY_ROUTING_KEY);

            // 声明重试交换机和队列
            channel.exchangeDeclare(MqConstant.RETRY_EXCHANGE_NAME, "direct");
            Map<String, Object> retryArgs = new HashMap<>();
            // 最大重试次数
            retryArgs.put("x-max-length", 3);
            // 超过重试次数转发到死信队列
            retryArgs.put("x-dead-letter-exchange", BiMqConstant.BI_EXCHANGE_NAME);
            retryArgs.put("x-dead-letter-routing-key", BiMqConstant.BI_ROUTING_KEY);

            channel.queueDeclare(MqConstant.RETRY_QUEUE_NAME, true, false, false, retryArgs);
            channel.queueBind(MqConstant.RETRY_QUEUE_NAME,
                    MqConstant.RETRY_EXCHANGE_NAME,
                    MqConstant.RETRY_ROUTING_KEY);

            // 声明消息追踪队列
            channel.exchangeDeclare(MqConstant.TRACE_EXCHANGE_NAME, "fanout");
            channel.queueDeclare(MqConstant.TRACE_QUEUE_NAME, true, false, false, null);
            channel.queueBind(MqConstant.TRACE_QUEUE_NAME,
                    MqConstant.TRACE_EXCHANGE_NAME,
                    MqConstant.TRACE_ROUTING_KEY);
        }
    }
}