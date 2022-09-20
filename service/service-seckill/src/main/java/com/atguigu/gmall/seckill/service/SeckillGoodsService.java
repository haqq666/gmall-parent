package com.atguigu.gmall.seckill.service;


import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【seckill_goods】的数据库操作Service
* @createDate 2022-09-20 00:07:11
*/
public interface SeckillGoodsService extends IService<SeckillGoods> {

    List<SeckillGoods> getCurrentSecKillList();

    List<SeckillGoods> getCurrentSecKillListFromCache();

    SeckillGoods getSeckillDetail(Long skuId);

    void deduceSeckillGoods(Long skuId);
}
