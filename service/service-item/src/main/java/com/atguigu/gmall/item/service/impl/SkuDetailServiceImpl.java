package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.config.ThreadPool.ThreadPoolAutoConfiguration;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeign;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:43
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Resource
    SkuDetailFeign skuDetailFeign;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public SkuDetailsTo getSkuDetailsTo(Long skuId) {
//        Result<SkuDetailsTo> result = skuDetailFeign.getSkuDetailTo(skuId);
        SkuDetailsTo skuDetailsTo = new SkuDetailsTo();
        // private SkuInfo skuInfo;
        CompletableFuture<SkuInfo> infoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> skuDetailToInfo = skuDetailFeign.getSkuDetailToInfo(skuId);
            SkuInfo info = skuDetailToInfo.getData();
            skuDetailsTo.setSkuInfo(info);
            return skuDetailToInfo.getData();
        }, executor);

        /**
         * 查图片列表
         */
        infoFuture.thenAcceptAsync((info) -> {
            Result<List<SkuImage>> skuInfoImageList = skuDetailFeign.getSkuInfoImageList(skuId);
            info.setSkuImageList(skuInfoImageList.getData());
        },executor);

        CompletableFuture<Void> categoryFuture = infoFuture.thenAcceptAsync((info) -> {
            //    private CategoryViewTo categoryView;
            Long category3Id = info.getCategory3Id();
            Result<CategoryViewTo> viewToResult = skuDetailFeign.getSkuDetailToCategoryTree(category3Id);
            skuDetailsTo.setCategoryView(viewToResult.getData());
        }, executor);

        CompletableFuture<Void> saleAttrListFuture = infoFuture.thenAcceptAsync((info) -> {
            //    private List<SpuSaleAttr> spuSaleAttrList;
            Long spuId = info.getSpuId();
            Result<List<SpuSaleAttr>> skuDetailToSaleAttrList = skuDetailFeign.getSkuDetailToSaleAttrList(spuId, skuId);
            List<SpuSaleAttr> saleAttrList = skuDetailToSaleAttrList.getData();
            skuDetailsTo.setSpuSaleAttrList(saleAttrList);
        }, executor);

        CompletableFuture<Void> jsonFuture = infoFuture.thenAcceptAsync((info) -> {
            //    private String valuesSkuJson;
            Long spuId = info.getSpuId();
            Result<String> skuJsonRes = skuDetailFeign.getSkuDetailToValuesSkuJson(spuId);
            skuDetailsTo.setValuesSkuJson(skuJsonRes.getData());
        });

        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> priceRes = skuDetailFeign.getSkuDetailTo1010price(skuId);
            skuDetailsTo.setPrice(priceRes.getData());
        }, executor);

        CompletableFuture.allOf(jsonFuture,priceFuture,saleAttrListFuture,categoryFuture).join();

        return skuDetailsTo;
    }
}
