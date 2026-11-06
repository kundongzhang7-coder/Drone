package com.drone.config;

import com.drone.mq.MQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置：检测任务异步处理队列 + 死信队列。
 * 消费失败且重试耗尽的消息进入 DLX，避免丢失并便于人工排查。
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange detectionExchange() {
        return new TopicExchange(MQConstants.EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(MQConstants.DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue detectionQueue() {
        return QueueBuilder.durable(MQConstants.QUEUE)
                .withArgument("x-dead-letter-exchange", MQConstants.DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", MQConstants.DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(MQConstants.DLX_QUEUE).build();
    }

    @Bean
    public Binding detectionBinding(Queue detectionQueue, TopicExchange detectionExchange) {
        return BindingBuilder.bind(detectionQueue).to(detectionExchange).with(MQConstants.ROUTING_KEY);
    }

    @Bean
    public Binding dlxBinding(Queue dlxQueue, TopicExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(MQConstants.DLX_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                        Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
