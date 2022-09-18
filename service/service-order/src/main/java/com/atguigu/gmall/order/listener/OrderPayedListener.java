package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.constant.MQConst;
import com.atguigu.gmall.rabbit.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.lang.model.type.ArrayType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/18 16:25
 */
@Slf4j
@Component
public class OrderPayedListener {

    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitService rabbitService;
    @Autowired
    OrderInfoService orderInfoService;

    @RabbitListener(queues = MQConst.QUEUE_ORDER_PAYED)
    public void payedOrderListener(Message message, Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        Map<String,String> map = Jsons.toObj(message, Map.class);

        //修改订单状态
        String tradeNo = map.get("trade_no");
        try {
            //保存订单
            PaymentInfo paymentInfo = paymentInfoService.savePaymentInfo(map);
            //修改订单状态

            orderInfoService.changeOrderStatus(
                    paymentInfo.getOrderId(),
                    paymentInfo.getUserId(),
                    ProcessStatus.PAID,
                    Arrays.asList(ProcessStatus.CLOSED,ProcessStatus.UNPAID));
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            rabbitService.retryConsumMsg(deliveryTag,
                    SysRedisConstant.MQ_RETRY + "orderPayed:" + tradeNo,
                    10L,
                    channel);
        }



    }
}
