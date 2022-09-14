package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;
import java.util.List;

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
        Result<Object> result = cartFeignClient.addCarthtml(skuId, skuNum);

        if (result.isOk()){
            model.addAttribute("skuInfo",result.getData());
            model.addAttribute("skuNum",skuNum);
            return "cart/addCart";
        }else {
            model.addAttribute("msg",result.getData());
            return "cart/error";
        }

    }

    @GetMapping("cart.html")
    public String toCart(){

        return "cart/index";
    }

    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){

        cartFeignClient.deleteChecked();

        return "redirect:http://cart.gmall.com/cart.html";
    }


}
