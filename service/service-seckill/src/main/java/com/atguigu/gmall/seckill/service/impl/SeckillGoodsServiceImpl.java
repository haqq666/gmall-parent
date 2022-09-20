package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【seckill_goods】的数据库操作Service实现
* @createDate 2022-09-20 00:07:11
*/
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
    implements SeckillGoodsService{

    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    SeckillGoodsCacheOpsService cacheOpsService;

    @Override
    public List<SeckillGoods> getCurrentSecKillList() {
        String date = DateUtil.formatDate(new Date());
        return seckillGoodsMapper.getCurrentSecKillList(date);
    }

    @Override
    public List<SeckillGoods> getCurrentSecKillListFromCache() {

        return cacheOpsService.getSecKillGoods();
    }

    @Override
    public SeckillGoods getSeckillDetail(Long skuId) {

        return  cacheOpsService.getSeckillDetail(skuId);
    }

    @Override
    public void deduceSeckillGoods(Long skuId) {
        seckillGoodsMapper.deduceSeckillGoods(skuId);
    }


}




