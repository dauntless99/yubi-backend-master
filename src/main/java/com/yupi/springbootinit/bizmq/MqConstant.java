// src/main/java/com/yupi/springbootinit/bizmq/Extended// 新增 MqConstant.java
package com.yupi.springbootinit.bizmq;

/**
 * 扩展 扩展的消息队列常量定义
 */
public interface MqConstant {
    // 延迟队列相关
    String DELAY_EXCHANGE_NAME = "delay_exchange";
    String DELAY_QUEUE_NAME = "delay_queue";
    String DELAY_ROUTING_KEY = "delay_routingKey";

    // 死信队列相关
    String RETRY_EXCHANGE_NAME = "retry_exchange";
    String RETRY_QUEUE_NAME = "retry_queue";
    String RETRY_ROUTING_KEY = "retry_routingKey";

    // 消息追踪相关
    String TRACE_EXCHANGE_NAME = "trace_exchange";
    String TRACE_QUEUE_NAME = "trace_queue";
    String TRACE_ROUTING_KEY = "trace_routingKey";
}