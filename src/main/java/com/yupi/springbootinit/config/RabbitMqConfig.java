// src/main/java/com/yupi/springbootinit/config/
// 新增 RabbitMqConfig.java
package com.yupi.springbootinit.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置扩展
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 配置重试消费者容器
     */
    @Bean
    public SimpleRabbitListenerContainerFactory retryListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动确认
        factory.setConcurrentConsumers(2); // 并发消费者数量
        factory.setMaxConcurrentConsumers(5); // 最大并发消费者数量
        factory.setPrefetchCount(1); // 预取数量
        return factory;
    }

    /**
     * 配置追踪消费者容器
     */
    @Bean
    public SimpleRabbitListenerContainerFactory traceListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO); // 自动确认
        factory.setConcurrentConsumers(1);
        return factory;
    }
}