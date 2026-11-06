package com.drone.mq;

/**
 * RabbitMQ 交换机、队列、路由键统一约定
 */
public final class MQConstants {

    private MQConstants() {
    }

    public static final String EXCHANGE = "detection.exchange";
    public static final String QUEUE = "detection.queue";
    public static final String ROUTING_KEY = "detection.task";

    public static final String DLX_EXCHANGE = "detection.dlx.exchange";
    public static final String DLX_QUEUE = "detection.dlx.queue";
    public static final String DLX_ROUTING_KEY = "detection.dlx";
}
