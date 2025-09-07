package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
public class BiInitMain {

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel(); // 仅使用一个Channel变量

            // 原业务交换机和队列
            String EXCHANGE_NAME = BiMqConstant.BI_EXCHANGE_NAME;
            String QUEUE_NAME = BiMqConstant.BI_QUEUE_NAME;
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

            // 死信交换机和队列
            String DLX_EXCHANGE = "bi_dlx_exchange";
            String DLX_QUEUE = "bi_dlx_queue";
            // 声明死信交换机
            channel.exchangeDeclare(DLX_EXCHANGE, "direct", true);
            // 声明死信队列
            channel.queueDeclare(DLX_QUEUE, true, false, false, null);
            // 绑定死信队列到死信交换机
            channel.queueBind(DLX_QUEUE, DLX_EXCHANGE, "bi_dlx_routingKey");

            // 为业务队列绑定死信交换机
            Map<String, Object> args1 = new HashMap<>();
            // 启用死信配置，将业务队列与死信交换机关联
            args1.put("x-dead-letter-exchange", DLX_EXCHANGE);
            args1.put("x-dead-letter-routing-key", "bi_dlx_routingKey");
            // 队列最多存1条消息，超过则老消息进入死信
            args1.put("x-max-length", 1);
            // 可以添加消息过期时间，例如10秒后过期
            args1.put("x-message-ttl", 10000);

            // 声明业务队列并应用配置
            channel.queueDeclare(QUEUE_NAME, true, false, false, args1);
            // 绑定业务队列到业务交换机
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);

            System.out.println("队列和交换机配置完成");

            // 关闭资源
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
