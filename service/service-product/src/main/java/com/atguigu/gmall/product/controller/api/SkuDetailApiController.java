package com.atguigu.gmall.product.controller.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

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

//    @GetMapping("getSkuDetailTo/{skuId}")
//    public Result<SkuDetailsTo> getSkuDetailTo(@PathVariable("skuId")Long skuId){
//        SkuDetailsTo skuDetailsTo = skuInfoService.getSkuDetailTo(skuId);
//        return Result.ok(skuDetailsTo);
//    }

    /**
     *   private SkuInfo skuInfo;
     * @param skuId
     * @return
     */
    @GetMapping("getSkuDetailTo/info/{skuId}")
    public Result<SkuInfo> getSkuDetailToInfo(@PathVariable("skuId")Long skuId){
        SkuInfo info = skuInfoService.getSkuDetailToInfo(skuId);
        return Result.ok(info);
    }

    /**
     * private CategoryViewTo categoryView
     * @param c3Id
     * @return
     */
    @GetMapping("/getSkuDetailTo/categoryTree/{c3Id}")
    public Result<CategoryViewTo> getSkuDetailToCategoryTree(@PathVariable("c3Id")Long c3Id){
        CategoryViewTo CategoryViewTo = skuInfoService.getSkuDetailToCategoryTree(c3Id);
       return Result.ok(CategoryViewTo);
    }
    /**
     *  private List<SpuSaleAttr> spuSaleAttrList;
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("getSkuDetailTo/saleAttrList/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSkuDetailToSaleAttrList(
                            @PathVariable("spuId")Long spuId,
                            @PathVariable("skuId")Long skuId){
        List<SpuSaleAttr> valueList = skuInfoService.getSpuDetailToSaleAttrList(spuId,skuId);
        return Result.ok(valueList);
    }

    //    private String valuesSkuJson;
    @GetMapping("getSkuDetailTo/valuesSkuJson/{spuId}")
    public Result<String> getSkuDetailToValuesSkuJson(@PathVariable("spuId")Long spuId){
       String valueJson =  skuInfoService.getSkuDetailToValuesSkuJson(spuId);
       return Result.ok(valueJson);
    }

    //    private BigDecimal price;
    @GetMapping("/getSkuDetailTo/1010price/{skuId}")
    public Result<BigDecimal> getSkuDetailTo1010price(@PathVariable("skuId")Long skuId){
       BigDecimal price =  skuInfoService.getSkuDetailTo1010price(skuId);
       return Result.ok(price);
    }

    @GetMapping("getSkuDetailTo/imageList/{skuId}")
    public Result<List<SkuImage>> getSkuInfoImageList(@PathVariable("skuId")Long skuId){
        List<SkuImage> imageList = skuInfoService.getSkuInfoImageList(skuId);
        return Result.ok(imageList);
    }


}
