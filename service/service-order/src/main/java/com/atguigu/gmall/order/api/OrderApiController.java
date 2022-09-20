package com.atguigu.gmall.order.api;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 19:29
 */
@RequestMapping("/api/inner/rpc/order")
@RestController
public class OrderApiController {

    @Autowired
    OrderBizService orderBizService;
    @Autowired
    OrderInfoService orderInfoService;

    @GetMapping("/confirm/data")
    public Result<OrderConfirmDataVo> confirmData(){
        OrderConfirmDataVo orderConfirmDataVo  = orderBizService.getConfirmData();
        return Result.ok(orderConfirmDataVo);
    }

    @GetMapping("/info/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable("orderId")Long orderId){

        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId,userId)
                .eq(OrderInfo::getId,orderId);
        OrderInfo one = orderInfoService.getOne(wrapper);
        return Result.ok(one);
    }
    @PostMapping("/submitSeckillOrder")
    public Result<Long> submitSeckillOrder(@RequestBody OrderInfo orderInfo){

        Long orderId = orderInfoService.submitSeckillOrder(orderInfo);

        return Result.ok(orderId);
    }
}
