package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 20:29
 */
@FeignClient("service-search")
@RequestMapping("/api/inner/rpc/search")
public interface SearchFeignClient {

    @PostMapping("/goods")
    Result saveGoods(@RequestBody Goods goods);

    @PostMapping("/deleteGoodds/{skuId}")
    Result deleteGoods(@PathVariable("skuId")Long skuId);
}