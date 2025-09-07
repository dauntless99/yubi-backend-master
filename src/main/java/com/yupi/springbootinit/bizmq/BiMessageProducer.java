package com.yupi.springbootinit.bizmq;

import com.alibaba.excel.util.StringUtils;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    // 发送消息前校验非空
    public void sendMessage(String message) {
        if (StringUtils.isBlank(message)) {
            System.out.println("发送的消息为空，拒绝发送");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        }
        // 发送持久化消息（确保消息不丢失）
        rabbitTemplate.convertAndSend(
                BiMqConstant.BI_EXCHANGE_NAME,
                BiMqConstant.BI_ROUTING_KEY,
                message,
                msg -> {
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return msg;
                }
        );
    }
}