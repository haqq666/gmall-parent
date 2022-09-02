package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.ItemFeignClient;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 20:49
 */
@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;

    @GetMapping("{skuId}.html")
    public String item(@PathVariable("skuId")Long skuId, Model model){

        Result<SkuDetailsTo> result = itemFeignClient.SkuDetailsTo(skuId);

        if (result.isOk()){
            SkuDetailsTo skuDetailsTo = result.getData();
            if (skuDetailsTo == null || skuDetailsTo.getSkuInfo() == null){
                return "item/404";
            }
            model.addAttribute("categoryView",skuDetailsTo.getCategoryView());
            model.addAttribute("skuInfo", skuDetailsTo.getSkuInfo());
            model.addAttribute("spuSaleAttrList",skuDetailsTo.getSpuSaleAttrList());
            model.addAttribute("valuesSkuJson",skuDetailsTo.getValuesSkuJson());
            model.addAttribute("price",skuDetailsTo.getPrice());
        }
        return "item/index";

    }
}
