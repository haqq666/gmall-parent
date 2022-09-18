package com.atguigu.gmall.rabbit;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/18 21:57
 */
@Slf4j
@Service
public class RabbitService {

    @Autowired
    StringRedisTemplate redisTemplate;

    public void retryConsumMsg(Long messageTag, String uniqtKey, Long maxNum, Channel channel) throws IOException {
        Long aLong = redisTemplate.opsForValue().increment(uniqtKey);
        if (aLong <= maxNum){
            channel.basicNack(messageTag,true,true);
        }else {
            channel.basicNack(messageTag,true,false);
            redisTemplate.delete(uniqtKey);
            log.error("消息{}，重试{}次 是失败，已存到数据库",messageTag,maxNum);
        }
    }
}
