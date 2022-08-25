package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 20:43
 */
@RestController
@RequestMapping("/admin/product")
public class BaseSaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 获取所有销售属性的名字
     * @return
     */
    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }

    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId")Long spuId){

        List<SpuSaleAttr> list = spuSaleAttrService.spuSaleAttrList(spuId);
        return Result.ok(list);
    }

}
