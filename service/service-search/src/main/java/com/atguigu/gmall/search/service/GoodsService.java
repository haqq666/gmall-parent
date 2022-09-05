package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 20:26
 */
public interface GoodsService {
    void saveGoods(Goods goods);

    void deleteGoods(Long skuId);
}
