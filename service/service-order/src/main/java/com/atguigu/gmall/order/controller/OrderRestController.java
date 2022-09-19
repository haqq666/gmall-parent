package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo")String tradeNo,
                              @RequestBody OrderSubmitVo submitVo){
        Long orderId = orderBizService.submitOrder(tradeNo,submitVo);
        return Result.ok(orderId.toString());
    }

    @GetMapping("/{pn}/{ps}")
    public Result getOrderPage(@PathVariable("pn")Integer pn,
                               @PathVariable("ps")Integer ps){

        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();

        Page<OrderInfo> page = new Page<>(pn,ps);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, userId);
        Page<OrderInfo> infoPage = orderInfoService.page(page, wrapper);

        //查询订单的详细信息
        infoPage.getRecords().stream()
                .parallel()
                .forEach(info ->{
                    List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(info.getId(), userId);
                    info.setOrderDetailList(orderDetails);
                });
        return Result.ok(infoPage);
    }
}
