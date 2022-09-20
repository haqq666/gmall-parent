package com.atguigu.gmall.seckill.biz;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.seckill.SeckillConfirmVo;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 9:32
 */
public interface SeckillBizService {
    String getgetSeckillCode(Long skuId);

    ResultCodeEnum getSeckillOrder(Long skuId, String skuIdStr);

    ResultCodeEnum checkSeckillOrderStatus(Long skuId);

    SeckillConfirmVo SeckillConfirmVo(Long skuId);

    Long submitSeckillOrder(OrderInfo orderInfo);
}
