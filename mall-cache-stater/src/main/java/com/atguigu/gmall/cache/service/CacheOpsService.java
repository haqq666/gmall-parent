package com.atguigu.gmall.cache.service;


import java.lang.reflect.Type;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/31 23:23
 */
public interface CacheOpsService {

    Object getCacheData(String key, Type type);

    /**
     * 从缓存中获取数据转为普通的数据类型
     * @param key
     * @param clz
     * @param <T>
     * @return
     */
    <T> T getCacheData(String key, Class<T> clz);

    Boolean getBloomContains(String bloomName, Object bloomValue);

    Boolean getBloomContains(Object skuId);

    Boolean tryLock(Long skuId);

    void saveCacheData(String key, Object obj);

    void unlock(Long skuId);

    Boolean tryLock(String lockName);

    void unlock(String lockName);
}
