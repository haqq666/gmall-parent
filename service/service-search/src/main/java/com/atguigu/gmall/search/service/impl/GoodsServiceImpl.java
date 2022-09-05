package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 20:27
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsRepository goodsRepository;

    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void deleteGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }
}