package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 0:07
 */
@RequestMapping("/api/inner/rpc/cart")
@RestController
public class CartApiController {

    @GetMapping("/addCart")
    public Result<SkuInfo> addCarthtml(@RequestParam("skuId")Long skuId,
                                       @RequestParam("skuNum")Integer skuNum,
                                       @RequestHeader(value = "userId",required = false)Long userId){
        System.out.println("service-cart: 用户的Id为" + userId);
        return Result.ok();

    }

}
