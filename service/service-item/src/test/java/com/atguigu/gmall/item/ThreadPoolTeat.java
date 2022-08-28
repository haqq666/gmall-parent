package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/28 23:55
 */
@SpringBootTest
public class ThreadPoolTeat {

    @Autowired
    ThreadPoolExecutor executor;

    @Test
    public void poolTest(){
        executor.submit(()->{
            System.out.println(Thread.currentThread().getName() + "hhh");
        });
    }

    @Test
    public void moreThreadTest(){
        for (int i = 0; i < 20; i++) {
            executor.submit(()->{
                System.out.println(Thread.currentThread().getName() + "hhh");
            });
        }
    }
}
