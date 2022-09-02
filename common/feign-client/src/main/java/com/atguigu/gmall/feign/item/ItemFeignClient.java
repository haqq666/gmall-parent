package com.atguigu.gmall.feign.item;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:23
 */
@RequestMapping("api/inner/rpc/item")
@FeignClient("service-item")
public interface ItemFeignClient {

    @GetMapping("{skuId}")
    Result<SkuDetailsTo> SkuDetailsTo(@PathVariable("skuId")Long skuId);

}
