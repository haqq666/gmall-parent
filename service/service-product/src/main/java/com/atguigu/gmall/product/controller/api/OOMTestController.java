package com.atguigu.gmall.product.controller.api;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.result.Result;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/29 22:48
 */
@RestController
public class OOMTestController {

    Map<String,String> map = new  HashMap();
    @Autowired
    RedissonClient redissonClient;
    @GetMapping("/hello")
    public Result hello(){
        String replace = UUID.randomUUID().toString().replace("-", "");
        map.put(replace,replace);
        return Result.ok();
    }
    @GetMapping("/bloomTest/{skuId}")
    public Result bloomTest(@PathVariable("skuId")Long skuId){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(SysRedisConstant.BlOOM_SKUID);
        boolean contains = bloomFilter.contains(skuId);
        return Result.ok("布隆中有吗？" + contains);
    }


}
