package com.yupi.springbootinit.mq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
public class ExampleSend {

    //声明队列名字，新的连接工厂，通道，channel的声明队列（队列名，是否持久化，是否唯一，是否自动删除，参数MAP），
    // 然后通道发布，配置交换机名字（队列名字），路由键，基本参数，消息体
    private final static String QUEUE_NAME = "helloWorld";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            //String exchange, String routingKey, AMQP.BasicProperties props, byte[] body
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        }
    }
}