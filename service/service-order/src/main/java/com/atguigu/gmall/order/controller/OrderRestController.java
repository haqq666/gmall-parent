package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 22:40
 */
@RequestMapping("/api/order/auth")
@RestController
public class OrderRestController {

    @Autowired
    OrderBizService orderBizService;
   //api/order/auth/submitOrder?tradeNo=1663088512012_3
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo")String tradeNo,
                              @RequestBody OrderSubmitVo submitVo){
        Long orderId = orderBizService.submitOrder(tradeNo,submitVo);
        return Result.ok(orderId.toString());
    }
}
