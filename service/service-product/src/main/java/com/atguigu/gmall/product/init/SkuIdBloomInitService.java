package com.atguigu.gmall.product.init;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 1:35
 */
@Slf4j
@Service
public class SkuIdBloomInitService {
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    RedissonClient redissonClient;

    @PostConstruct
    public void initSkuBloom(){

        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConstant.BlOOM_SKUID);
        if (filter.isExists()){
            log.info("布隆存在");
           return;
        }
        log.info("布隆初始化进行中");
        filter.tryInit(5000000,0.00001);
        //查出商品的skuid
        List<Long> skuIds = skuInfoService.getAllSkuId();
        for (Long skuId : skuIds) {
            filter.add(skuId);
        }

        log.info("布隆初始化完成 添加了{}条数据",skuIds.size());
    }
}
