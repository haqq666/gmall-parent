package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 19:34
 */
public interface OrderBizService {

    OrderConfirmDataVo getConfirmData();

    Long submitOrder(String tradeNo, OrderSubmitVo submitVo);
}
