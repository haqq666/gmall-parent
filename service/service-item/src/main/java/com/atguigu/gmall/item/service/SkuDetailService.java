package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailsTo;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:42
 */
public interface SkuDetailService {
    SkuDetailsTo getSkuDetailsTo(Long skuId);
}
