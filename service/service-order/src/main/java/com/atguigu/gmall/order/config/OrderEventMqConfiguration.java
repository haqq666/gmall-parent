package com.atguigu.gmall.order.config;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.constant.MQConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * 交换机的定义
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/14 19:02
 */
@Configuration
public class OrderEventMqConfiguration {


    /**
     * 项目启动发现没有这个交换机就会自动创建出来
     * 订单事件交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange(
                MQConst.EXCHANGE_ORDER_EVENT,
                true,
                false);
    }

    //延时队列
    @Bean
    public Queue orderDelayQueue(){
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl", SysRedisConstant.ORDER_CLOSE_TTL*1000);
        arguments.put("x-dead-letter-exchange",MQConst.EXCHANGE_ORDER_EVENT);
        arguments.put("x-dead-letter-routing-key",MQConst.RK_ORDER_DEAD);

        return new Queue(MQConst.QUEUE_ORDER_DELAY,
                true,false,false,arguments);
    }
    //绑定机制
    @Bean
    public Binding orderDelayQueueBinding(){
      return new Binding(
              MQConst.QUEUE_ORDER_DELAY,
              Binding.DestinationType.QUEUE,
              MQConst.EXCHANGE_ORDER_EVENT,
              MQConst.RK_ORDER_CREATED,
              null);
    }

    //死信队列
    @Bean
    public Queue deadQueue(){
       return new Queue(MQConst.QUEUE_ORDER_DEAD,
               true,false,false);
    }
    //死信队列绑定
    @Bean
    public Binding orderDeadQueueBinding(){
        return new Binding(MQConst.QUEUE_ORDER_DEAD,
                Binding.DestinationType.QUEUE,
                MQConst.EXCHANGE_ORDER_EVENT,
                MQConst.RK_ORDER_DEAD,null);
    }

    //订单支付成功队列
    @Bean
    public Queue orderPayedQueue(){
        return new Queue(
                MQConst.QUEUE_ORDER_PAYED,
                true,
                false,
                false);
    }

    @Bean
    public Binding orderPayedQueueBinding(){
        return new Binding(
                MQConst.QUEUE_ORDER_PAYED,
                Binding.DestinationType.QUEUE,
                MQConst.EXCHANGE_ORDER_EVENT,
                MQConst.RK_ORDER_PAYED,
                null);
    }


}






