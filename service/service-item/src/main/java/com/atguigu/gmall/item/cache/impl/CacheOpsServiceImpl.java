package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
    public Boolean getBloomContains(Long skuId) {
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
    public void unlock(Long skuId) {
        String catchKey = SysRedisConstant.LOCK_SKU_DETAILS + skuId;
        RLock rLock = redissonClient.getLock(catchKey);

        rLock.unlock();
    }
}
