package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import jdk.internal.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 0:07
 */
@RequestMapping("/api/inner/rpc/cart")
@RestController
public class CartApiController {

    @Autowired
    CartService cartService;

    @GetMapping("/addCart")
    public Result<Object> addCarthtml(@RequestParam("skuId") Long skuId,
                                       @RequestParam("skuNum") Integer skuNum) {
        SkuInfo skuInfo = cartService.addCarthtml(skuId,skuNum);

         return Result.ok(skuInfo);

    }

    @GetMapping("/cart/deleteChecked")
    public Result deleteChecked(){
        String cartKey = cartService.getCartKey();
        cartService.deleteChecked(cartKey);
        return Result.ok();
    }

}
