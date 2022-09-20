package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MQConst;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.SeckillTempMsg;
import com.atguigu.gmall.rabbit.RabbitService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 20:25
 */
@Slf4j
@Component
public class WaitingSecKillListener {

    @Autowired
    SeckillGoodsService seckillGoodsService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitService rabbitService;
    @Autowired
    RabbitTemplate rabbitTemplate;


    @RabbitListener( bindings = {
            @QueueBinding(
                value = @Queue(value = MQConst.QUEUE_SECKILL_ORDERWAIT,durable = "true",exclusive = "false",autoDelete = "false"),
                exchange = @Exchange(value = MQConst.EXCHANGE_SECKILL_EVENT,durable = "true",autoDelete = "false",type = "topic"),
                key = MQConst.RK_SECKILL_ORDERWAIT)
    })
    public void waitingSecKill(Message message, Channel channel) throws IOException {
        Long tag = message.getMessageProperties().getDeliveryTag();
        SeckillTempMsg tempMsg = Jsons.toObj(message, SeckillTempMsg.class);
        Long skuId = tempMsg.getSkuId();
        log.info("监听到秒杀扣库存消息... {}",tempMsg);

        //减库存
        try {
            seckillGoodsService.deduceSeckillGoods(skuId);
            //减库存成功，发送消息
            rabbitTemplate.convertAndSend(MQConst.EXCHANGE_ORDER_EVENT,
                    MQConst.RK_ORDER_SECKILLOK,
                    Jsons.toStr(tempMsg));
            //修改redis标志位
            String code = SysRedisConstant.CACHE_SECKILL_GOODS_ORDER + tempMsg.getStrCode();
            String json = redisTemplate.opsForValue().get(code);
            OrderInfo orderInfo = Jsons.toObj(json, OrderInfo.class);
            orderInfo.setOperateTime(new Date());
            redisTemplate.opsForValue().set(code,Jsons.toStr(orderInfo));

            channel.basicAck(tag,false);
        }catch (DataIntegrityViolationException e){
            log.error("库存扣减失败 ");
            //将redis的订单状态改为x
            String code = SysRedisConstant.CACHE_SECKILL_GOODS_ORDER + tempMsg.getStrCode();
            redisTemplate.opsForValue().set(code,"x");
            channel.basicAck(tag,false);
        }catch (Exception ex){
            log.error("业务失败：{}",ex);
            String uqKey = SysRedisConstant.MQ_RETRY + tempMsg.getStrCode();
            rabbitService.retryConsumMsg(tag,uqKey,10L,channel);
        }




    }
}
