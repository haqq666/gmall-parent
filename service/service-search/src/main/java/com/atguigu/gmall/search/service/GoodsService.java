package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 20:26
 */
public interface GoodsService {
    void saveGoods(Goods goods);

    void deleteGoods(Long skuId);

    SearchResponseVo search(SearchParamVo searchParamVo);

    void updateHotScore(Long skuId, Long score);
}
