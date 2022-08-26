package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeign;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 21:43
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Resource
    SkuDetailFeign skuDetailFeign;
    @Override
    public SkuDetailsTo getSkuDetailsTo(Long skuId) {
        Result<SkuDetailsTo> result = skuDetailFeign.getSkuDetailTo(skuId);
        return result.getData();
    }
}
