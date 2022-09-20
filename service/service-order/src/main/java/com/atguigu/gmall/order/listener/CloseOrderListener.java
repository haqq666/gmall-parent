package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.constant.MQConst;
import com.atguigu.gmall.rabbit.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/14 20:12
 */
@Slf4j
@Component
public class CloseOrderListener {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    OrderBizService orderBizService;
    @Autowired
    RabbitService rabbitService;

    @RabbitListener(queues = MQConst.QUEUE_ORDER_DEAD)
    public void closeOrderListener(Message message, Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        OrderMsg orderMsg = Jsons.toObj(message, OrderMsg.class);
        try {
            log.info("监听到超时订单{}，正在关闭",orderMsg.getOrderId());
            orderBizService.closeOrder(orderMsg.getOrderId(),orderMsg.getUserId());
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {

            rabbitService.retryConsumMsg(
                    deliveryTag,
                    SysRedisConstant.MQ_RETRY + "order:" + orderMsg.getOrderId(),
                    10L,
                    channel);
        }
    }

}