package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.annotation.EnableGlobalException;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/7 23:57
 */
@EnableGlobalException
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product"})
@SpringCloudApplication
public class CartMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class,args);
    }
}
