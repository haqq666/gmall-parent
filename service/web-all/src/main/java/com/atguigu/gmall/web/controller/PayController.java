package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/14 1:39
 */
@Controller
public class PayController {
    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/pay.html")
    public String payPage(Model model,
                          @RequestParam("orderId") Long orderId){

       Result<OrderInfo> orderInfo = orderFeignClient.getOrderInfo(orderId);
        Date expireTime = orderInfo.getData().getExpireTime();
        Date date = new Date();
        if (date.before(expireTime)){
            model.addAttribute("orderInfo",orderInfo.getData());
            return "payment/pay";
        }
        return "payment/error";
    }

    @GetMapping("/pay/success.html")
    public String success(){

        return "payment/success";
    }
}
