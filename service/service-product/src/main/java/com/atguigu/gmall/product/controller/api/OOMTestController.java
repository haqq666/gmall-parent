package com.atguigu.gmall.product.controller.api;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/29 22:48
 */
@RestController
public class OOMTestController {

    Map<String,String> map = new  HashMap();

    @GetMapping("/hello")
    public Result hello(){
        String replace = UUID.randomUUID().toString().replace("-", "");
        map.put(replace,replace);
        return Result.ok();
    }

}
