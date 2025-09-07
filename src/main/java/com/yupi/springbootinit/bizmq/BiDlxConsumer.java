package com.yupi.springbootinit.bizmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BiDlxConsumer {
    @RabbitListener(queues = "bi_dlx_queue")
    public void handleDlxMessage(String message) {
        log.error("死信队列收到失败消息: {}", message);
        // 1. 记录到数据库，等待人工处理
        // 2. 定时任务重试（如通过定时任务重新发送到业务队列）
    }
}