package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 20:45
 */
@RestController
@RequestMapping("api/inner/rpc/item")
public class ItemController {
    @Autowired
    SkuDetailService skuDetailService;
    @GetMapping("{skuId}")
    public Result<SkuDetailsTo> skuDetailsTo(@PathVariable("skuId")Long skuId){
        SkuDetailsTo skuDetailsTo = skuDetailService.getSkuDetailsTo(skuId);
        return Result.ok(skuDetailsTo);
    }

    @GetMapping("/aaa")
    public Result aaa(){
        return Result.ok();
    }
}
