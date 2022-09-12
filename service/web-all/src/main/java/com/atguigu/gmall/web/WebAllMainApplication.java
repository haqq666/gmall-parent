package com.atguigu.gmall.web;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 16:43
 */
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.item",
        "com.atguigu.gmall.feign.search",
        "com.atguigu.gmall.feign.cart"})
@SpringCloudApplication
@EnableFeignInterceptorConfiguration
public class WebAllMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class, args);
    }
}
