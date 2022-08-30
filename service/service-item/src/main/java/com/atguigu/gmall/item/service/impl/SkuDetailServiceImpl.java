package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.config.ThreadPool.ThreadPoolAutoConfiguration;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.feign.SkuDetailFeign;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:43
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeign skuDetailFeign;

    @Autowired
    ThreadPoolExecutor executor;

    //本地缓存
   Map<Long,SkuDetailsTo> skuCache = new ConcurrentHashMap<>();

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 使用Redis缓存 100/100 -2619/s
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailsTo getSkuDetailsTo(Long skuId) {

        String jsonStr = stringRedisTemplate.opsForValue().get("sku:info:" + skuId);
        //以前缓存过，数据库没有记录，
        if ("x".equals(jsonStr)){
            return null;
        }
        if (StringUtils.isEmpty(jsonStr)){
            SkuDetailsTo skuDetailsToRPC = getSkuDetailsToRPC(skuId);
            String cacheJson = "x";
            if (skuDetailsToRPC !=null){
                stringRedisTemplate.opsForValue().set("sku:info:" + skuId, Jsons.toStr(skuDetailsToRPC),7, TimeUnit.DAYS);
            }else {
                stringRedisTemplate.opsForValue().set("sku:info:" + skuId, Jsons.toStr(skuDetailsToRPC),30, TimeUnit.MINUTES);
            }
            return skuDetailsToRPC;
        }

        SkuDetailsTo skuDetailsTo = Jsons.toObj(jsonStr, SkuDetailsTo.class);
        return skuDetailsTo;
    }


    /**
     * 试用本地缓存 -100/100 4700/s
     * getSkuDetailsToLocalCache
     * @param skuId
     * @return
     */
    public SkuDetailsTo getSkuDetailsToLocalCache(Long skuId) {

        SkuDetailsTo skuDetailsTo = skuCache.get(skuId);
        if (skuDetailsTo == null){
            //没命中：
            SkuDetailsTo skuDetailsToRPC = getSkuDetailsToRPC(skuId);
            //将数据保存到缓存中
            skuCache.put(skuId,skuDetailsToRPC);

            return skuDetailsToRPC;
        }

        return skuDetailsTo;
    }




    /**
     * 未引入缓存  -100/100  -208/s
     * @param skuId
     * @return
     */
    public SkuDetailsTo getSkuDetailsToRPC(Long skuId) {
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
        CompletableFuture<Void>  imageFuture = infoFuture.thenAcceptAsync((info) -> {
            Result<List<SkuImage>> skuInfoImageList = skuDetailFeign.getSkuInfoImageList(skuId);
            info.setSkuImageList(skuInfoImageList.getData());
        }, executor);

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

        CompletableFuture
                .allOf(jsonFuture,priceFuture,saleAttrListFuture,categoryFuture,imageFuture)
                .join();

        return skuDetailsTo;
    }
    /**
     * 串行
     * @param skuId
     * @return
     */
    public SkuDetailsTo getSkuDetailsToSerial(Long skuId) {
        SkuDetailsTo skuDetailsTo = new SkuDetailsTo();

        Result<SkuInfo> skuDetailToInfo = skuDetailFeign.getSkuDetailToInfo(skuId);
        SkuInfo info = skuDetailToInfo.getData();
        skuDetailsTo.setSkuInfo(info);

        Result<List<SkuImage>> skuInfoImageList = skuDetailFeign.getSkuInfoImageList(skuId);
        info.setSkuImageList(skuInfoImageList.getData());

        Long category3Id = info.getCategory3Id();
        Result<CategoryViewTo> viewToResult = skuDetailFeign.getSkuDetailToCategoryTree(category3Id);
        skuDetailsTo.setCategoryView(viewToResult.getData());

        Long spuId = info.getSpuId();
        Result<List<SpuSaleAttr>> skuDetailToSaleAttrList = skuDetailFeign.getSkuDetailToSaleAttrList(spuId, skuId);
        List<SpuSaleAttr> saleAttrList = skuDetailToSaleAttrList.getData();
        skuDetailsTo.setSpuSaleAttrList(saleAttrList);

        Result<String> skuJsonRes = skuDetailFeign.getSkuDetailToValuesSkuJson(spuId);
        skuDetailsTo.setValuesSkuJson(skuJsonRes.getData());

        Result<BigDecimal> priceRes = skuDetailFeign.getSkuDetailTo1010price(skuId);
        skuDetailsTo.setPrice(priceRes.getData());



        return skuDetailsTo;
    }

}
