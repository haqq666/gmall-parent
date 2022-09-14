package com.atguigu.gmall.model.vo.order;

import com.atguigu.gmall.model.user.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 20:17
 */
@Data
public class OrderConfirmDataVo {

    private List<CartInfoVo> detailArrayList;
    private List<UserAddress> userAddressList;
    private Integer totalNum;
    private BigDecimal totalAmount;

    private String tradeNo;
}
