package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 20:10
 */
@RequestMapping("/api/inner/rpc/order")
@FeignClient("service-order")
public interface OrderFeignClient {

    @GetMapping("/confirm/data")
    Result<OrderConfirmDataVo> confirmData();

    @GetMapping("/info/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable("orderId")Long orderId);
}
