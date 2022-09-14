package com.atguigu.gmall.order;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptorConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/12 22:18
 */
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.cart",
                                "com.atguigu.gmall.feign.product",
                                "com.atguigu.gmall.feign.user",
                                "com.atguigu.gmall.feign.ware"})
@EnableFeignInterceptorConfiguration
@EnableTransactionManagement
@MapperScan("com.atguigu.gmall.order.mapper")
@SpringCloudApplication
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class,args);
    }
}
