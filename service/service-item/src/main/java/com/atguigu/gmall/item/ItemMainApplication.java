package com.atguigu.gmall.item;

import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import com.atguigu.gmall.common.config.ThreadPool.ThreadPoolAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 20:27
 */
@EnableThreadPool
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product"})
@SpringCloudApplication
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
