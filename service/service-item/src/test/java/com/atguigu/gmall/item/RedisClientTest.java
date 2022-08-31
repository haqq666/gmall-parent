package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/31 20:27
 */
@SpringBootTest
public class RedisClientTest {

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void test01(){
        System.out.println(redissonClient);
    }

}
