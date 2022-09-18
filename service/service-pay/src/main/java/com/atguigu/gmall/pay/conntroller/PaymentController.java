package com.atguigu.gmall.pay.conntroller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.pay.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/16 20:05
 */
@Slf4j
@RequestMapping("api/payment")
@Controller
public class PaymentController {

    @Autowired
    AlipayService alipayService;

    @ResponseBody
    @GetMapping("/alipay/submit/{orderId}")
    public String alipaySubmit(@PathVariable("orderId")Long orderId) throws AlipayApiException {

       String page = alipayService.getAliPayPageHtml(orderId);
        return page;
    }

    @GetMapping("/pay/success")
    public String paySuccess(@RequestParam Map<String,String> payParam) throws AlipayApiException {

       boolean b = alipayService.alrsaCheckV1(payParam);
       if (b){
           log.info("同步通知抵达。支付成功，验签通过。数据：{}", Jsons.toStr(payParam));
       }

        return "redirect:http://gmall.com/pay/success.html";
    }

    @ResponseBody
    @RequestMapping("/notify/success")
    public String notifySuccess(@RequestParam Map<String,String> payParam) throws AlipayApiException {

        boolean b = alipayService.alrsaCheckV1(payParam);
        if (b){
            log.info("异步通知抵达。支付成功，验签通过。数据：{}", Jsons.toStr(payParam));
            //TODO 改订单信息
            alipayService.sendPayedMsg(payParam);
        }else {
            return "error";
        }

        return "success";
    }

}
