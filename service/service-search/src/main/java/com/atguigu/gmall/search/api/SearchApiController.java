package com.atguigu.gmall.search.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 20:22
 */
@RestController
@RequestMapping("/api/inner/rpc/search")
public class SearchApiController {

    @Autowired
    GoodsService goodsService;

    @PostMapping("/goods")
    public Result saveGoods(@RequestBody Goods goods){

        goodsService.saveGoods(goods);
        return Result.ok();
    }

    @PostMapping("/deleteGoodds/{skuId}")
    public Result deleteGoods(@PathVariable("skuId")Long skuId){
        goodsService.deleteGoods(skuId);
        return Result.ok();
    }

    @PostMapping("/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo){
        SearchResponseVo searchResponseVo = goodsService.search(searchParamVo);
        return Result.ok(searchResponseVo);
    }

    @GetMapping("/goods/hotscore/{skuId}")
    public Result updateHotScore(@PathVariable("skuId")Long skuId,
                                 @RequestParam("score")Long score){
        goodsService.updateHotScore(skuId,score);
        return Result.ok();
    }
}
