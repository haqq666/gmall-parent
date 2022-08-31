package com.atguigu.gmall.item.cache;

import com.atguigu.gmall.model.to.SkuDetailsTo;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/31 23:23
 */
public interface CacheOpsService {
    <T> T getCacheData(String key, Class<T> clz);

    Boolean getBloomContains(Long skuId);

    Boolean tryLock(Long skuId);

    void saveCacheData(String key, Object obj);

    void unlock(Long skuId);
}
