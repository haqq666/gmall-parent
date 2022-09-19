package com.atguigu.gmall.order.service;


import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【order_detail(订单明细表)】的数据库操作Service
* @createDate 2022-09-12 22:10:51
*/
public interface OrderDetailService extends IService<OrderDetail> {

    List<OrderDetail> getOrderDetails(Long orderId, Long userId);
}
