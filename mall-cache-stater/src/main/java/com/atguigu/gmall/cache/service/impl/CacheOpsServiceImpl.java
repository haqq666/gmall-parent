package com.atguigu.gmall.cache.service.impl;


import com.atguigu.gmall.cache.constant.SysRedisConstant;
import com.atguigu.gmall.cache.service.CacheOpsService;
import com.atguigu.gmall.cache.utils.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import jodd.util.StringUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/31 23:24
 * 封装缓存操作
 */
@Service
public class CacheOpsServiceImpl implements CacheOpsService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    @Override
    public Object getCacheData(String key, Type type) {
        String jsonStr = redisTemplate.opsForValue().get(key);
        if (SysRedisConstant.NULL_VAL.equals(jsonStr)){
            return null;
        }
        Object obj = Jsons.toObj(jsonStr, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return type;
            }
        });
        return obj;
    }

    /**
     * 从缓存中获取数据并转成指定类型的数据
     * @param key
     * @param clz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getCacheData(String key, Class<T> clz) {
        String jsonStr = redisTemplate.opsForValue().get(key);
        if (SysRedisConstant.NULL_VAL.equals(jsonStr)){
            return null;
        }
        T t = Jsons.toObj(jsonStr, clz);
        return t;
    }

    @Override
    public Boolean getBloomContains(String bloomName, Object bloomValue) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomName);
        return bloomFilter.contains(bloomValue);

    }

    /**
     * 获取bloom
     * @param skuId
     * @return
     */
    @Override
    public Boolean getBloomContains(Object skuId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(SysRedisConstant.BlOOM_SKUID);
        return bloomFilter.contains(skuId);
    }



    @Override
    public Boolean tryLock(Long skuId) {
        String catchKey = SysRedisConstant.LOCK_SKU_DETAILS + skuId;
        RLock rLock = redissonClient.getLock(catchKey);
        boolean lock = rLock.tryLock();
        return lock;
    }

    @Override
    public void saveCacheData(String key, Object obj) {
        if (obj == null){
            redisTemplate.opsForValue() .set(key,
                    SysRedisConstant.NULL_VAL,
                    SysRedisConstant.NULL_VAL_TTL,
                    TimeUnit.SECONDS);
        }else {
            String objJson = Jsons.toStr(obj);
            redisTemplate.opsForValue().set(key,
                    objJson,
                    SysRedisConstant.SKUDETAILS_VAL_TTL,
                    TimeUnit.SECONDS);
        }


    }

    @Override
    public void saveCacheData(String key, Object obj, Long ttl) {
        if (obj == null){
            redisTemplate.opsForValue() .set(key,
                    SysRedisConstant.NULL_VAL,
                    SysRedisConstant.NULL_VAL_TTL,
                    TimeUnit.SECONDS);
        }else {
            String objJson = Jsons.toStr(obj);
            redisTemplate.opsForValue().set(key,
                    objJson,
                   ttl,
                    TimeUnit.SECONDS);
        }
    }

    @Override
    public void unlock(Long skuId) {
        String catchKey = SysRedisConstant.LOCK_SKU_DETAILS + skuId;
        RLock rLock = redissonClient.getLock(catchKey);

        rLock.unlock();
    }

    @Override
    public Boolean tryLock(String lockName) {
        RLock rLock = redissonClient.getLock(lockName);
        return rLock.tryLock();

    }

    @Override
    public void unlock(String lockName) {
        RLock rLock = redissonClient.getLock(lockName);
        rLock.unlock();
    }

    @Override
    public void delay2delete(String cacheKey) {
        redisTemplate.delete(cacheKey);

        scheduledExecutorService.schedule(()->{
            redisTemplate.delete(cacheKey);
        },5,TimeUnit.SECONDS);
    }
}
