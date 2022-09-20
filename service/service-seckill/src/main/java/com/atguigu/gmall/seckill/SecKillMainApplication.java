package com.atguigu.gmall.seckill;

import com.atguigu.gmall.annotation.EnableAppRabbit;
import com.atguigu.gmall.common.annotation.EnableFeignInterceptorConfiguration;
import com.atguigu.gmall.common.annotation.EnableGlobalException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 0:01
 */
@EnableFeignInterceptorConfiguration
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.user",
                                    "com.atguigu.gmall.feign.order"})
@EnableAppRabbit
@EnableGlobalException
@EnableScheduling
@MapperScan("com.atguigu.gmall.seckill.mapper")
@SpringCloudApplication
public class SecKillMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillMainApplication.class,args);
    }
}
