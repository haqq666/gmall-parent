package com.atguigu.gmall.order.service.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
* @author 乔豆麻担
* @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
* @createDate 2022-09-12 22:10:52
*/
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{

    @Autowired
    OrderInfoService orderInfoService;

    @Transactional
    @Override
    public PaymentInfo savePaymentInfo(Map<String, String> map) {

        PaymentInfo paymentInfo = new PaymentInfo();
        String outTradeNo = map.get("out_trade_no");
        paymentInfo.setOutTradeNo(outTradeNo);
        //获取用户id
        long userId = Long.parseLong(outTradeNo.split("_")[1]);
        paymentInfo.setUserId(userId);

        //查询订单信息,确保订单已保存
        PaymentInfo one = getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getUserId, userId)
                .eq(PaymentInfo::getOutTradeNo, outTradeNo));

        if (one != null){
            return one;
        }

        //订单未保存
        OrderInfo orderInfo = orderInfoService.getOrderInfoByOutTradeNumberAndUserId(outTradeNo,userId);

        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType("ALIPAY");
        paymentInfo.setTradeNo(map.get("trade_no"));
        paymentInfo.setTotalAmount(new BigDecimal(map.get("total_amount")));
        paymentInfo.setSubject(map.get("subject"));
        paymentInfo.setPaymentStatus(map.get("trade_status"));
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(DateUtil.parseDate(map.get("notify_time"),"yyyy-MM-dd HH:mm:ss"));

        paymentInfo.setCallbackContent(Jsons.toStr(map));

        save(paymentInfo);

        return paymentInfo;
    }
}




