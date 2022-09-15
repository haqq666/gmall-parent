package com.atguigu.gmall.rabbit;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/14 18:52
 */
public class MQConst {

    public static final String EXCHANGE_ORDER_EVENT = "order-event-exchange";
    public static final String RK_ORDER_CREATED = "order.creat";
    public static final String QUEUE_ORDER_DELAY = "order-delay-queue";
    public static final String RK_ORDER_DEAD = "order.dead";
    public static final String QUEUE_ORDER_DEAD = "order-dead-queue";
}
