package com.drone.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 检测任务消息生产者
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(DetectionMessage message) {
        rabbitTemplate.convertAndSend(MQConstants.EXCHANGE, MQConstants.ROUTING_KEY, message);
        log.info("已发送检测任务消息 taskId={}", message.getTaskId());
    }
}
