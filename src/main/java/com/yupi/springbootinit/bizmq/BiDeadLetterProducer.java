package com.yupi.springbootinit.bizmq;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BiDeadLetterProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_DEAD_QUEUE_NAME, BiMqConstant.BI_DEAD_ROUTING_KEY, message);
    }

}
