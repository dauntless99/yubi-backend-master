package com.yupi.springbootinit.mq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ExampleRecv {
    private final static String QUEUE_NAME = "helloWorld";
    //消费者，新建连接工程，创建连接通道，声明队列参数，设置发送回调函数
    //消息队列默认发送后就立即删除消息，这容易导致消息丢失。
    //所以1.可以启用消息确认机制，如果一个工作队列被终止了，那可以发给另一个工作队列。
    //消费者的送达确认会强制设置超时时间（默认为 30 分钟）。这有助于检测那些始终未确认送达的、存在故障（卡住）的消费者。您可以按照 送达确认超时中的说明增加此超时时间。
    //手动消息确认默认开启。在之前的示例中，我们通过autoAck=true 标志明确关闭了它。现在是时候设置此标志，false并在任务完成后从工作器发送适当的确认了。
    //手动ack必须记得写代码，不然消息未被确认无法被释放。


    //对于丢失的问题，将消息标记为持久化并不能完全保证消息不会丢失。虽然它告诉 RabbitMQ 将消息保存到磁盘，
    // 但 RabbitMQ 仍然会在短时间内接收消息但尚未保存。此外，RabbitMQ 并非fsync(2)对每条消息都这样做——它可能只是被保存到缓存中，
    // 而并未真正写入磁盘。持久化保证并不强，但对于我们这个简单的任务队列来说已经足够了。
    // 如果您需要更强的保证，可以使用 发布者确认 (publisher confirmed)。
    //简单来说，设置队列声明durable只是在缓存级别，而不是磁盘级别。要更强保证用publisher confirmed。

    //int prefetchCount = 1;
    //channel.basicQos(prefetchCount);均匀发送给消费者

    //在 Java 客户端中，当我们不提供任何参数时，queueDeclare() 我们会创建一个具有生成名称的非持久、独占、自动删除的队列：
    //String queueName = channel.queueDeclare().getQueue();创建临时队列
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}