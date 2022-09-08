package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 0:01
 */
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    @GetMapping("/addCart.html")
    public String addCarthtml(@RequestParam("skuId")Long skuId,
                              @RequestParam("skuNum")Integer skuNum,
                              Model model){
        Result<SkuInfo> result = cartFeignClient.addCarthtml(skuId, skuNum);

        model.addAttribute("skuInfo",result.getData());
        model.addAttribute("skuNum",skuNum);
        return "cart/addCart";
    }

}
