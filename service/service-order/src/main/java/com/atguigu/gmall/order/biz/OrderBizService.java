package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.order.OrderWareMapVo;
import com.atguigu.gmall.model.vo.order.WareChildOrderVo;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 19:34
 */
public interface OrderBizService {

    OrderConfirmDataVo getConfirmData();

    Long submitOrder(String tradeNo, OrderSubmitVo submitVo);

    void closeOrder(Long orderId, Long userId);

    List<WareChildOrderVo> orderSplit(OrderWareMapVo vo);
}
