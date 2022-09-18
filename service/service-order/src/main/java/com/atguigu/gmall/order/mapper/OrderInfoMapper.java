package com.atguigu.gmall.order.mapper;


import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【order_info(订单表 订单表)】的数据库操作Mapper
* @createDate 2022-09-12 22:10:51
* @Entity com.atguigu.gmall.order.domain.OrderInfo
*/
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {


    void changeOrderStatus(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("orderStatus") String orderStatus, @Param("processStatus") String processStatus, @Param("expire") List<String> expire);



}




