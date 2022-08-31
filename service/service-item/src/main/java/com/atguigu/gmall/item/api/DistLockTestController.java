package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.lock.RedisDistLock;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/31 18:45
 */
@RestController
@RequestMapping("lock")
public class DistLockTestController {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 锁的测试                      --值       --测试数据 线程数/循环数   --QPS (吞吐量)
     * 1.0：不加锁                  --- 307         --100/100              --2656.7s
     * 2.0：加本地锁                ---10000         --100/100              --733.0s
     * 2.1:分布式环境下本地锁测试      --5178          --100/100              --554.5s
     * 3.0:分布式锁                 --10000          --100/100             --83.6s
     *
     */

    @Autowired
    RedisDistLock redisDistLock;     //分布式锁

    @Autowired
    RedissonClient redissonClient;

//    ReentrantLock lock = new ReentrantLock();//本地锁
    @GetMapping("add")
    public Result increment(){
        String token = redisDistLock.lock();
        String a = redisTemplate.opsForValue().get("a");
        int i = Integer.parseInt(a);
        i++;
        redisTemplate.opsForValue().set("a",i+"");
        System.out.println(i);
        redisDistLock.unlock(token);
        return Result.ok();
    }

    /**
     * 普通锁测试
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/common")
    public Result common() throws InterruptedException {
        RLock lock = redissonClient.getLock("lock");
        lock.lock();
        System.out.println("获得锁");
        System.out.println("执行业务");
        Thread.sleep(5000);
        System.out.println("解锁");
        lock.unlock();
        return Result.ok();
    }
    int i = 0;

    /**
     * 读写锁之读锁测试
     * @return
     */
    @GetMapping("/read")
    public Result readLock(){
        RReadWriteLock rReadWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = rReadWriteLock.readLock();
        rLock.lock();
        int x = i;
        rLock.unlock();
        return Result.ok(x);
    }

    /**
     * 读写锁之写锁
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/write")
    public Result writeLock() throws InterruptedException {
        RReadWriteLock rReadWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock wLock = rReadWriteLock.writeLock();
        wLock.lock();
        i = 888;
        Thread.sleep(5000);
        wLock.unlock();
        return Result.ok();
    }

    @GetMapping("/longzhu")
    public Result longzhu(){
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("sl-lock");
        countDownLatch.countDown();
        return Result.ok("收集到一个龙珠");
    }

    /**
     *闭锁
     * @return
     * @throws InterruptedException
     */
    @GetMapping("shenlong")
    public Result shenlong() throws InterruptedException {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("sl-lock");
        countDownLatch.trySetCount(7);
        countDownLatch.await();
        return Result.ok("神龙来了.....");
    }





}
