package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 1:13
 */
public interface SeckillGoodsCacheOpsService {

    void upSecKillGoods(List<SeckillGoods> list);

    void clearSecKillGoods();

    List<SeckillGoods> getSecKillGoods();

    SeckillGoods getSeckillDetail(Long skuId);
}
