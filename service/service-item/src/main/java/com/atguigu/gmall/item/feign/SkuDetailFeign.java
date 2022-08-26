package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:46
 */
@RequestMapping("api/inner/rpc/product")
@FeignClient("service-product")
public interface SkuDetailFeign {

    @GetMapping("getSkuDetailTo/{skuId}")
    Result<SkuDetailsTo> getSkuDetailTo(@PathVariable("skuId")Long skuId);

}
