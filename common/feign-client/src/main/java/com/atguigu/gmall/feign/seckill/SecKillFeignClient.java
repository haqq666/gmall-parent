package com.atguigu.gmall.feign.seckill;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.vo.seckill.SeckillConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 0:26
 */
@FeignClient("service-seckill")
@RequestMapping("api/inner/rpc/seckill")
public interface SecKillFeignClient {

    @GetMapping("current/secKill/list")
    Result<List<SeckillGoods>> getCurrentSecKillList();

    @GetMapping("seckill/detail/{skuId}")
    Result<SeckillGoods> getSeckillDetail(@PathVariable("skuId")Long skuId);

    @GetMapping("seckill/confirmvo/{skuId}")
    Result<SeckillConfirmVo> SeckillConfirmVo(@PathVariable("skuId")Long skuId);

}
