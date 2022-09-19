package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MQConst;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.WareDeduceMsg;
import com.atguigu.gmall.model.to.mq.WareDeduceStatusMsg;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.rabbit.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/19 1:18
 */
@Slf4j
@Service
public class OrderStockDeduceListener {

    @Autowired
    RabbitService rabbitService;
    @Autowired
    OrderInfoService orderInfoService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MQConst.QUEUE_WARE_ORDER,
                                    durable = "true",exclusive = "false",autoDelete = "false"),
                    exchange = @Exchange(
                            name = MQConst.EXCHANGE_WARE_ORDER,
                            type = "direct", durable = "true", autoDelete = "false"),
                    key = MQConst.RK_WARE_ORDER
            )
    })
    public void stockDeduceListener(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        WareDeduceStatusMsg msg = Jsons.toObj(message, WareDeduceStatusMsg.class);
        Long orderId = msg.getOrderId();

        try {
            log.info("订单服务【修改订单出库状态】 监听到库存扣减结果：{}",msg);
            //修改库存状态
            //查询订单信息
            OrderInfo info = orderInfoService.getById(orderId);

            //修改订单状态
            ProcessStatus status = null;
            switch (msg.getStatus()){
                case "DEDUCTED": status = ProcessStatus.WAITING_DELEVER;break;
                case "OUT_OF_STOCK": status=ProcessStatus.STOCK_OVER_EXCEPTION;break;
                default: status= ProcessStatus.PAID;
            }

            orderInfoService.changeOrderStatus(orderId,info.getUserId(),status, Arrays.asList(ProcessStatus.PAID));

            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            String uk = SysRedisConstant.MQ_RETRY + "stock:order:deduce:" + orderId;
            rabbitService.retryConsumMsg(deliveryTag,uk,10L,channel);
        }

    }

}
