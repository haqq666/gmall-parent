package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 18:43
 */
@Service
public class BloomOpsServiceImpl implements BloomOpsService {

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SkuInfoService skuInfoService;

    @Override
    public void rebuild(String bloomName, BloomDataQueryService bloomDataQueryService) {
        //拿到老布隆
        RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(bloomName);
        //创建新的布隆
        String nBloomName = bloomName = "_new";
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(nBloomName);
        //获取布隆要存的值
        //List<Long> allSkuId = skuInfoService.getAllSkuId();
        List list = bloomDataQueryService.queryData();
        //初始化布隆
        bloomFilter.tryInit(5000000,0.00001);
        //保存值
        for (Object skuId : list) {
            bloomFilter.add(skuId);
        }
        //改名
        oldBloomFilter.rename("tempBloom");
        bloomFilter.rename(bloomName);

        //删旧的布隆和临时的布隆
        oldBloomFilter.deleteAsync();
        redissonClient.getBloomFilter("tempBloom").deleteAsync();
    }
}
