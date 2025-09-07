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
            Channel channel = connection.createChannel();

            // 原业务交换机和队列
            String EXCHANGE_NAME = BiMqConstant.BI_EXCHANGE_NAME;
            String QUEUE_NAME = BiMqConstant.BI_QUEUE_NAME;
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 死信交换机和队列
            String DLX_EXCHANGE = "bi_dlx_exchange";
            String DLX_QUEUE = "bi_dlx_queue";
            channel.exchangeDeclare(DLX_EXCHANGE, "direct");
            channel.queueDeclare(DLX_QUEUE, true, false, false, null);
            channel.queueBind(DLX_QUEUE, DLX_EXCHANGE, "bi_dlx_routingKey");

            // 为业务队列绑定死信交换机
            Map<String, Object> args1 = new HashMap<>();
            args1.put("x-dead-letter-exchange", DLX_EXCHANGE); // 死信交换机
            args1.put("x-dead-letter-routing-key", "bi_dlx_routingKey"); // 死信路由键
            args1.put("x-max-retry-count", 3); // 自定义参数：最大重试次数
            // 创建队列，随机分配一个队列名称
            //public AMQP.Queue.DeclareOk queueDeclare(String queue, boolean durable, boolean exclusive,
            // boolean autoDelete, Map<String, Object> arguments) throws IOException {
                channel.queueDeclare(QUEUE_NAME, true, false, false, args1);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
