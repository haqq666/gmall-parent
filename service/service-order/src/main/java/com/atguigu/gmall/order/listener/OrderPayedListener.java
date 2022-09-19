package com.atguigu.gmall.order.listener;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.StoreDeduceSkuInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.model.to.mq.WareDeduceMsg;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.constant.MQConst;
import com.atguigu.gmall.rabbit.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    OrderDetailService orderDetailService;

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

            //通知库存系统扣减库存
            WareDeduceMsg wareDeduceMsg = prepareWareDeduceMsg(paymentInfo);
            rabbitTemplate.convertAndSend(
                    MQConst.EXCHANGE_WARE_EVENT,
                    MQConst.RK_DEDUCE_STOCK,
                    Jsons.toStr(wareDeduceMsg));

            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            rabbitService.retryConsumMsg(deliveryTag,
                    SysRedisConstant.MQ_RETRY + "orderPayed:" + tradeNo,
                    10L,
                    channel);
        }



    }

    private WareDeduceMsg prepareWareDeduceMsg(PaymentInfo paymentInfo) {
        WareDeduceMsg wareDeduceMsg = new WareDeduceMsg();
        Long orderId = paymentInfo.getOrderId();
        wareDeduceMsg.setOrderId(orderId);
        Long userId = paymentInfo.getUserId();

        OrderInfo orderInfo = orderInfoService.getOrderInfoByOrderIdAndUserId(orderId,userId);

        wareDeduceMsg.setConsignee(orderInfo.getConsignee());
        wareDeduceMsg.setConsigneeTel(orderInfo.getConsigneeTel());
        wareDeduceMsg.setOrderComment(orderInfo.getOrderComment());
        wareDeduceMsg.setOrderBody(orderInfo.getTradeBody());
        wareDeduceMsg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        wareDeduceMsg.setPaymentWay("2");


        List<OrderDetail> detailList = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getUserId, userId)
                .eq(OrderDetail::getOrderId, orderId));
        List<StoreDeduceSkuInfo> collect = detailList.stream().map(detail -> {
            StoreDeduceSkuInfo deduceSkuInfo = new StoreDeduceSkuInfo();
            deduceSkuInfo.setSkuId(detail.getSkuId());
            deduceSkuInfo.setSkuNum(detail.getSkuNum());
            deduceSkuInfo.setSkuName(detail.getSkuName());
            return deduceSkuInfo;
        }).collect(Collectors.toList());

        wareDeduceMsg.setDetails(collect);

        return wareDeduceMsg;
    }
}
