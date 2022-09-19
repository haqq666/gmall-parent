package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 20:09
 */
@Controller("/")
public class OrderController {

    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/trade.html")
    public String totTrade(Model model){
        OrderConfirmDataVo data = orderFeignClient.confirmData().getData();
        model.addAttribute("detailArrayList",data.getDetailArrayList());
        model.addAttribute("totalNum",data.getTotalNum());
        model.addAttribute("totalAmount",data.getTotalAmount());
        model.addAttribute("userAddressList",data.getUserAddressList());
        model.addAttribute("tradeNo",data.getTradeNo());

        return "order/trade";
    }

    @GetMapping("/myOrder.html")
    public String myOrderList(){

        return "order/myOrder";
    }
}
