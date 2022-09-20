package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import io.reactivex.rxjava3.core.Completable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 1:14
 */
@Service
public class SeckillGoodsCacheOpsServiceImpl implements SeckillGoodsCacheOpsService {

    @Autowired
    StringRedisTemplate redisTemplate;

    Map<Long,SeckillGoods> goodsCache = new HashMap<>();

    @Override
    public void upSecKillGoods(List<SeckillGoods> list) {

        String date = DateUtil.formatDate(new Date());
        //在redis中存一份
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SysRedisConstant.CACHE_SECKILL_GOODS + date);
        //两天后过期
        hashOps.expire(2,TimeUnit.DAYS);

        list.stream().forEach(secKillGoods ->{
            hashOps.put(secKillGoods.getSkuId()+"", Jsons.toStr(secKillGoods));

            String cacheKey = SysRedisConstant.CACHE_SECKILL_GOODS_STOCK + date;
           redisTemplate.opsForValue().setIfAbsent(cacheKey,secKillGoods.getStockCount()+"",1,TimeUnit.DAYS);

           //放入本地缓存
            goodsCache.put(secKillGoods.getSkuId(),secKillGoods);
        });


    }

    @Override
    public void clearSecKillGoods() {
        goodsCache.clear();
    }

    @Override
    public List<SeckillGoods> getSecKillGoods() {
        List<SeckillGoods> list = goodsCache.values().stream()
                .sorted(Comparator.comparing(SeckillGoods::getStartTime)
                ).collect(Collectors.toList());

        if (list == null || list.size() == 0){
            //同步redis
            syncLocalAndRedisCache();
            list= goodsCache.values().stream()
                    .sorted(Comparator.comparing(SeckillGoods::getStartTime)
                    ).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public SeckillGoods getSeckillDetail(Long skuId) {
        SeckillGoods seckillGoods = goodsCache.get(skuId);
        if (seckillGoods == null){
            syncLocalAndRedisCache();
            seckillGoods = goodsCache.get(skuId);
        }
        return seckillGoods;
    }

    public List<SeckillGoods> getSeckillGoodsFromRemote(){
        String date = DateUtil.formatDate(new Date());
        String cacheKey = SysRedisConstant.CACHE_SECKILL_GOODS + date;
        List<Object> list = redisTemplate.opsForHash().values(cacheKey);
        return list.stream()
                .map(item -> Jsons.toObj(item.toString(), SeckillGoods.class))
                .sorted(Comparator.comparing(SeckillGoods::getStartTime))
                .collect(Collectors.toList());
    }

    private void syncLocalAndRedisCache() {
        List<SeckillGoods> goods = getSeckillGoodsFromRemote();
        goods.stream().forEach(goodsItem->goodsCache.put(goodsItem.getSkuId(),goodsItem));
    }
}
