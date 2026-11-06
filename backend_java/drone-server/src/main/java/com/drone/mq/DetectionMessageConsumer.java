package com.drone.mq;

import com.drone.service.DetectionTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 检测任务消息消费者
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionMessageConsumer {

    private final DetectionTaskService taskService;

    @RabbitListener(queues = MQConstants.QUEUE)
    public void onDetectionMessage(DetectionMessage message) {
        log.info("收到检测任务消息 taskId={}", message.getTaskId());
        try {
            taskService.processOffline(message);
        } catch (Exception e) {
            log.error("检测任务消费失败，转入死信队列 taskId={}", message.getTaskId(), e);
            // 拒绝且不重新入队，借助队列的 DLX 配置进入死信队列
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = MQConstants.DLX_QUEUE)
    public void onDeadLetter(DetectionMessage message) {
        log.error("收到死信消息，标记任务为失败 taskId={}", message.getTaskId());
        taskService.markFailed(message.getTaskId());
    }
}
