package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.config.ThreadPool.ThreadPoolAutoConfiguration;
import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.item.feign.SkuDetailFeign;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:43
 */
@Slf4j
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
    @Autowired
    CacheOpsService cacheOpsService;

    /**
     * 查详情，最终优化  -100/100 --207.6
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailsTo getSkuDetailsTo(Long skuId) {
        //查缓存
        String key = SysRedisConstant.SKU_INFO_PREFIX + skuId;
        SkuDetailsTo cacheData= cacheOpsService.getCacheData(key,SkuDetailsTo.class);
        if (cacheData == null){
           Boolean contian = cacheOpsService.getBloomContains(skuId);
           //布隆说没有：直接返回
           if (!contian){
               log.info("{}布隆判定没有，检测到隐藏的攻击风险...",skuId);
               return null;
           }
           //布隆说有，加锁
            Boolean lock = cacheOpsService.tryLock(skuId);
           //抢到锁
            if (lock){
                log.info("[{}]缓存未命中，布隆说有，准备回源...",skuId);
                SkuDetailsTo skuDetailsToRPC = getSkuDetailsToRPC(skuId);
                cacheOpsService.saveCacheData(key,skuDetailsToRPC);
                cacheOpsService.unlock(skuId);
                return skuDetailsToRPC;
            }

            try {
                Thread.sleep(1000);
                return cacheOpsService.getCacheData(key,SkuDetailsTo.class);
            } catch (InterruptedException e) {
                return null;
            }
        }
        return cacheData;
    }


//    Map<Long,ReentrantLock> lockPoll = new ConcurrentHashMap<>(); //无法解决分布式问题
//    //ReentrantLock lock = new ReentrantLock(); 锁的力度太粗


//    /**
//     * redis锁的伪代码
//     */
//    private void redisLock(Long skuId){
//       //boolean b = setnx(skuId,1);//设置sku 1值为
//        //boolean b = setnxex(skuId,1，60s);//保证加锁和加时间原子性，防止意外发生自动解不了锁
//        String token = UUID.randomUUID().toString().replace("-", "");//唯一token防止解其他线程的锁；
//        boolean b = setnxex(skuId,token,60s);
//
//       if (b){
//           //查数据库
//           //解锁
//          String vison = redis.get(skuId);
//           if (vison.equals(token)) {
//               del("skuId");  //解锁也要有原子性  用脚本+eval脚本编。
//           }else {
//               //是别人的锁，你啥都别做
//           }
//
//       }else {
//          Thread.sleep(1000);
//          //查缓存；
//       }
//    }

    /**
     * 使用Redis缓存 100/100 -2619/s
     * @param skuId
     * @return
     */
//    public SkuDetailsTo getSkuDetailsTo(Long skuId) {
//        //从缓存中获取数据
//        String jsonStr = stringRedisTemplate.opsForValue().get("sku:info:" + skuId);
//        //以前缓存过，数据库没有记录，
//        if ("x".equals(jsonStr)){
//            return null;
//        }
//
//        if (StringUtils.isEmpty(jsonStr)){
//            SkuDetailsTo skuDetailsToRPC = null;
//            //解决击穿问题
//            //加锁
////          若map中没有则新建一个锁。若有则是新建一个锁
////            ReentrantLock lock = lockPoll.putIfAbsent(skuId, new ReentrantLock());
////            boolean b = lock.tryLock();
////            if (b){
//                //抢到锁了
//                skuDetailsToRPC = getSkuDetailsToRPC(skuId);
////            }else {
////                 jsonStr = stringRedisTemplate.opsForValue().get("sku:info:" + skuId);
////            }
//
//
//
//            String cacheJson = "x";
//            if (skuDetailsToRPC !=null){
//                stringRedisTemplate.opsForValue().set("sku:info:" + skuId, Jsons.toStr(skuDetailsToRPC),7, TimeUnit.DAYS);
//            }else {
//                //数据库里没有，缓存个值，防止恶意攻击，造成数据库崩溃
//                stringRedisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson,30, TimeUnit.MINUTES);
//            }
//            return skuDetailsToRPC;
//        }
//
//        SkuDetailsTo skuDetailsTo = Jsons.toObj(jsonStr, SkuDetailsTo.class);
//        return skuDetailsTo;
//    }


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
            if (info != null){
                Result<List<SkuImage>> skuInfoImageList = skuDetailFeign.getSkuInfoImageList(skuId);
                info.setSkuImageList(skuInfoImageList.getData());
            }

        }, executor);

        CompletableFuture<Void> categoryFuture = infoFuture.thenAcceptAsync((info) -> {
            //    private CategoryViewTo categoryView;
            if (info != null){
                Long category3Id = info.getCategory3Id();
                Result<CategoryViewTo> viewToResult = skuDetailFeign.getSkuDetailToCategoryTree(category3Id);
                skuDetailsTo.setCategoryView(viewToResult.getData());
            }
        }, executor);

        CompletableFuture<Void> saleAttrListFuture = infoFuture.thenAcceptAsync((info) -> {
            //    private List<SpuSaleAttr> spuSaleAttrList;
            if (info != null){
                Long spuId = info.getSpuId();
                Result<List<SpuSaleAttr>> skuDetailToSaleAttrList = skuDetailFeign.getSkuDetailToSaleAttrList(spuId, skuId);
                List<SpuSaleAttr> saleAttrList = skuDetailToSaleAttrList.getData();
                skuDetailsTo.setSpuSaleAttrList(saleAttrList);
            }

        }, executor);

        CompletableFuture<Void> jsonFuture = infoFuture.thenAcceptAsync((info) -> {
            //    private String valuesSkuJson;
            if (info != null){
                Long spuId = info.getSpuId();
                Result<String> skuJsonRes = skuDetailFeign.getSkuDetailToValuesSkuJson(spuId);
                skuDetailsTo.setValuesSkuJson(skuJsonRes.getData());
            }

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
