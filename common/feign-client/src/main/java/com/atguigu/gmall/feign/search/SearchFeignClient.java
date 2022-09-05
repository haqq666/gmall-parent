package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/search")
    Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo);

}