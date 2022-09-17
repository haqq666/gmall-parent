package com.atguigu.gmall.pay;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/16 19:50
 */
@EnableFeignInterceptorConfiguration
@EnableFeignClients({"com.atguigu.gmall.feign.order"})
@SpringCloudApplication
public class PayMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayMainApplication.class,args);
    }

}
