package com.atguigu.gmall.model.vo.seckill;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 23:17
 */
@Data
public class SeckillConfirmVo {

    private OrderInfo tempOrder;
    private List<UserAddress> userAddressList;
    private Integer totalNum;
    private BigDecimal totalAmount;

}
