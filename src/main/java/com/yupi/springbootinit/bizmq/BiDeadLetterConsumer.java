package com.yupi.springbootinit.bizmq;

import com.alibaba.excel.util.StringUtils;
import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.mapper.DeadLetterMessageMapper;
import com.yupi.springbootinit.model.entity.DeadLetterMessage;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class BiDeadLetterConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private BiMessageProducer biMessageProducer; // 用于重试发送

    // 死信消息表（需提前创建实体和Mapper）
    @Resource
    private DeadLetterMessageMapper deadLetterMessageMapper;

    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_DEAD_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveDeadLetter(@Payload(required = false)String message, Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        if (StringUtils.isBlank(message)) {
            log.warn("收到空的死信消息，deliveryTag: {}", deliveryTag);
            // 记录空消息到数据库（便于排查）
            DeadLetterMessage emptyMsg = new DeadLetterMessage();
            emptyMsg.setMessageContent("【空消息】");
            emptyMsg.setCreateTime(new Date());
            emptyMsg.setStatus(0); // 0-未处理
            deadLetterMessageMapper.insert(emptyMsg);
            // 确认消息（避免死信队列堆积）
            channel.basicAck(deliveryTag, false);
        }
        log.info("收到死信消息：{}", message);
        try {
            // 1. 记录到数据库，等待人工处理
            DeadLetterMessage deadLetter = new DeadLetterMessage();
            deadLetter.setMessageContent(message);
            deadLetter.setCreateTime(new Date());
            deadLetter.setStatus(0); // 0-未处理，1-已处理
            deadLetterMessageMapper.insert(deadLetter);

//            try {
//                // 重新发送到业务队列
//                biMessageProducer.sendMessage(deadLetter.getMessageContent());
//                // 更新状态为已处理
//                deadLetter.setStatus(1);
//                deadLetter.setUpdateTime(new Date());
//                deadLetterMessageMapper.updateById(deadLetter);
//            } catch (Exception e) {
//                log.error("重试消息失败：{}", message, e);
//            }

            // 2. 确认消息已接收（从死信队列移除）
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("死信处理失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    // 定时任务重试死信消息（每天凌晨2点执行）
    @Scheduled(cron = "0 0 2 * * ?")
    public void retryDeadLetterMessages() {
        log.info("开始重试死信消息");
        // 查询3天内未处理的死信消息
        Date threeDaysAgo = new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L);
        List<DeadLetterMessage> messages = deadLetterMessageMapper.listByStatusAndTime(0, threeDaysAgo);
        for (DeadLetterMessage msg : messages) {
            try {
                // 重新发送到业务队列
                biMessageProducer.sendMessage(msg.getMessageContent());
                // 更新状态为已处理
                msg.setStatus(1);
                msg.setUpdateTime(new Date());
                deadLetterMessageMapper.updateById(msg);
            } catch (Exception e) {
                log.error("重试消息失败：{}", msg.getId(), e);
            }
        }
    }
}