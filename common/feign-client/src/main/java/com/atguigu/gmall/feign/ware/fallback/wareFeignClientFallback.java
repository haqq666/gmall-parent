package com.atguigu.gmall.feign.ware.fallback;

import com.atguigu.gmall.feign.ware.WareFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/16 18:20
 */
@Component
public class wareFeignClientFallback implements WareFeignClient {

    @Override
    public String hasStock(Long skuId, Integer num) {

        return "1";
    }
}
