package com.atguigu.gmall.feign.user;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 20:45
 */
@FeignClient("service-user")
@RequestMapping("api/inner/rpc/user")
public interface UserFeignClient {

    @GetMapping("/user/address")
    Result<List<UserAddress>> getUserAddress();
}
