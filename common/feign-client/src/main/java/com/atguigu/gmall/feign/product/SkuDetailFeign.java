package com.atguigu.gmall.feign.product;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:46
 */
@RequestMapping("api/inner/rpc/product")
@FeignClient("service-product")
public interface SkuDetailFeign {

    @GetMapping("getSkuDetailTo/{skuId}")
    Result<SkuDetailsTo> getSkuDetailTo(@PathVariable("skuId")Long skuId);

    @GetMapping("getSkuDetailTo/info/{skuId}")
    Result<SkuInfo> getSkuDetailToInfo(@PathVariable("skuId")Long skuId);

    @GetMapping("/getSkuDetailTo/categoryTree/{c3Id}")
    Result<CategoryViewTo> getSkuDetailToCategoryTree(@PathVariable("c3Id")Long c3Id);

    @GetMapping("getSkuDetailTo/saleAttrList/{spuId}/{skuId}")
    Result<List<SpuSaleAttr>> getSkuDetailToSaleAttrList(
            @PathVariable("spuId")Long spuId,
            @PathVariable("skuId")Long skuId);

    @GetMapping("getSkuDetailTo/valuesSkuJson/{spuId}")
    Result<String> getSkuDetailToValuesSkuJson(@PathVariable("spuId")Long spuId);

    @GetMapping("/getSkuDetailTo/1010price/{skuId}")
    Result<BigDecimal> getSkuDetailTo1010price(@PathVariable("skuId")Long skuId);

    @GetMapping("getSkuDetailTo/imageList/{skuId}")
    public Result<List<SkuImage>> getSkuInfoImageList(@PathVariable("skuId")Long skuId);

    @GetMapping("/category/tree")
    Result<List<CategoryTreeTo>> getCategoryTreeTo();

}
