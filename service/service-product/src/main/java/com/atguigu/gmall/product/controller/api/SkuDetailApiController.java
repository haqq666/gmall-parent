package com.atguigu.gmall.product.controller.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:48
 */
@RestController
@RequestMapping("api/inner/rpc/product")
public class SkuDetailApiController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("getSkuDetailTo/{skuId}")
    public Result<SkuDetailsTo> getSkuDetailTo(@PathVariable("skuId")Long skuId){
        SkuDetailsTo skuDetailsTo = skuInfoService.getSkuDetailTo(skuId);
        return Result.ok(skuDetailsTo);
    }

}
