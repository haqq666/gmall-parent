package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 21:25
 */
@RequestMapping("/api/cart")
@RestController
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cartList")
    public Result<List<CartInfo>> getCartList() {
        String cartKey = cartService.getCartKey();

        cartService.MergeTempCartAndUserCart();
        List<CartInfo> cartInfoList = cartService.getCartList(cartKey);
        return Result.ok(cartInfoList);
    }

    @PostMapping("/addToCart/{skuId}/{num}")
    public Result updateCart(@PathVariable("skuId") Long skuId,
                             @PathVariable("num") Integer num) {
        String cartKey = cartService.getCartKey();
        cartService.updateCart(skuId, num, cartKey);
        return Result.ok();
    }

    @GetMapping("/checkCart/{skuId}/{status}")
    public Result checkCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("status") Integer status) {
        String cartKey = cartService.getCartKey();
        cartService.checkCart(skuId, status, cartKey);
        return Result.ok();
    }

    @DeleteMapping("deleteCart/{skuId}")
    public Result delete(@PathVariable("skuId") Long skuId) {
        String cartKey = cartService.getCartKey();
        cartService.delete(skuId, cartKey);
        return Result.ok();
    }


}
