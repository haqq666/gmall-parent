package com.atguigu.gmall.constant;

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
    public static final String RK_ORDER_PAYED = "order.payed";
    public static final String QUEUE_ORDER_PAYED = "order-payed-queue";
    public static final String EXCHANGE_WARE_EVENT = "exchange.direct.ware.stock";
    public static final String RK_DEDUCE_STOCK = "ware.stock";
    public static final String QUEUE_WARE_ORDER = "queue.ware.order";
    public static final String EXCHANGE_WARE_ORDER = "exchange.direct.ware.order";
    public static final String RK_WARE_ORDER = "ware.order";
}
