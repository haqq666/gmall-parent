package com.atguigu.gmall.seckill.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.vo.seckill.SeckillConfirmVo;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 0:17
 */
@RequestMapping("api/inner/rpc/seckill")
@RestController
public class SecKillApiController {

    @Autowired
    SeckillGoodsService seckillGoodsService;
    @Autowired
    SeckillBizService seckillBizService;

    @GetMapping("current/secKill/list")
    public Result<List<SeckillGoods>> getCurrentSecKillList(){
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.getCurrentSecKillListFromCache();
        return Result.ok(seckillGoodsList);
    }

    @GetMapping("seckill/detail/{skuId}")
    public Result<SeckillGoods> getSeckillDetail(@PathVariable("skuId")Long skuId){
        SeckillGoods seckillGoods = seckillGoodsService.getSeckillDetail(skuId);
        return Result.ok(seckillGoods);
    }

    @GetMapping("seckill/confirmvo/{skuId}")
    public Result<SeckillConfirmVo> SeckillConfirmVo(@PathVariable("skuId")Long skuId){
        SeckillConfirmVo vo = seckillBizService.SeckillConfirmVo(skuId);
        return Result.ok(vo);
    }



}
