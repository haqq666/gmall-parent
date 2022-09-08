package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 0:12
 */
@RequestMapping("/api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    @GetMapping("/addCart")
    Result<SkuInfo> addCarthtml(@RequestParam("skuId")Long skuId,
                                       @RequestParam("skuNum")Integer skuNum);
}
