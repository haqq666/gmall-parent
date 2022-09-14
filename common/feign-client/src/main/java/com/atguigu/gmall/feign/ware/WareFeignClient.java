package com.atguigu.gmall.feign.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 21:19
 */
@FeignClient(value = "ware-manage",url = "${app.ware-url:http://localhost:9001/}")
public interface WareFeignClient {

    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,
                    @RequestParam("num") Integer num);
}
