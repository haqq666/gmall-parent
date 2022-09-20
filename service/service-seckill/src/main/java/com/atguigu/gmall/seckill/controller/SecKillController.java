package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 9:29
 */
///api/activity/seckill/auth/getSeckillSkuIdStr/49
@RequestMapping("/api/activity/seckill/auth")
@RestController
public class SecKillController {
    @Autowired
    SeckillBizService bizService;

    @GetMapping("getSeckillSkuIdStr/{skuId}")
    public Result getSeckillCode(@PathVariable("skuId")Long skuId){
        String code = bizService.getgetSeckillCode(skuId);
        return Result.ok(code);
    }

    @PostMapping("seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId") Long skuId,
                               @RequestParam("skuIdStr")String skuIdStr){
        ResultCodeEnum codeEnum = bizService.getSeckillOrder(skuId,skuIdStr);
        return Result.build("",codeEnum);
    }

    @GetMapping("/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId")Long skuId){
        ResultCodeEnum codeEnum = bizService.checkSeckillOrderStatus(skuId);
        return Result.build("",codeEnum);
    }
    @PostMapping("/submitOrder")
    public Result submitOrder (@RequestBody OrderInfo orderInfo){
       Long orderId =  bizService.submitSeckillOrder(orderInfo);
        return Result.ok(orderId.toString());
    }

}
