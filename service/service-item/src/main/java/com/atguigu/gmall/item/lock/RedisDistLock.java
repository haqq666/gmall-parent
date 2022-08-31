package com.atguigu.gmall.item.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/31 18:44
 */
@Component
public class RedisDistLock {

    @Autowired
    StringRedisTemplate redisTemplate;

    public String lock(){
        String token = UUID.randomUUID().toString().replace("-", "");
        while (!redisTemplate.opsForValue().setIfAbsent("lock",token,10, TimeUnit.SECONDS)){

       }
       return token;
    }

    public void unlock(String token){
        String lua ="if redis.call('get',KEYS[1]) == ARGV[1]  then return redis.call('del',KEYS[1]); else  return 0;end;";
        redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class), Arrays.asList("lock"), token);
    }

}
