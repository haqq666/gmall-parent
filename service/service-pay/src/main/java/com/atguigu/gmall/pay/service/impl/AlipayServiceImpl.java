package com.atguigu.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.pay.config.AliPayConfigurationProperties;
import com.atguigu.gmall.pay.service.AlipayService;
import com.atguigu.gmall.constant.MQConst;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/17 0:27
 */
@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    AliPayConfigurationProperties properties;

    @Autowired
    OrderFeignClient orderFeignClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public String getAliPayPageHtml(Long orderId) throws AlipayApiException {

        //获取订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId).getData();

        if (orderInfo.getExpireTime().before(new Date())){
            throw new GmallException(ResultCodeEnum.ORDER_EXPIRE);
        }

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        //返回地址
        alipayRequest.setReturnUrl(properties.getReturnUrl());
        //通知地址
        alipayRequest.setNotifyUrl(properties.getNotifyUrl());



        //设置参数
        Map<String,Object> bazContent = new HashMap<>();
        bazContent.put("out_trade_no",orderInfo.getOutTradeNo()); //对外交易号
        bazContent.put("total_amount",orderInfo.getTotalAmount().toString()); //交易金额
        bazContent.put("subject","尚品汇订单：" + orderInfo.getOutTradeNo()); //订单名
        bazContent.put("body", orderInfo.getTradeBody()); //订单体
        bazContent.put("product_code","FAST_INSTANT_TRADE_PAY"); //订单类型

        String date = DateUtil.formatDate(orderInfo.getExpireTime(),"yyyy-MM-dd HH:mm:ss");
        bazContent.put("time_expire",date);

        alipayRequest.setBizContent(Jsons.toStr(bazContent));

        String result = alipayClient.pageExecute(alipayRequest).getBody();



        return result;
    }

    @Override
    public boolean alrsaCheckV1(Map<String, String> payParam) throws AlipayApiException {
        boolean signVerified = AlipaySignature.rsaCheckV1(
                payParam,
                properties.getAlipayPublicKey(),
                properties.getCharset(),
                properties.getSignType()); //调用SDK验证签名

        return signVerified;
    }

    //给订单交换机发送支付成功的消息
    @Override
    public void sendPayedMsg(Map<String, String> payParam) {
        rabbitTemplate.convertAndSend(
                MQConst.EXCHANGE_ORDER_EVENT,
                MQConst.RK_ORDER_PAYED,
                Jsons.toStr(payParam));
    }
}
