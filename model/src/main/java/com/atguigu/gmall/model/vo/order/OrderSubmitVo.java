package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 22:45
 */
@Data
public class OrderSubmitVo {

    private String consignee;
    private String consigneeTel;
    private String deliveryAddress;
    private String paymentWay;
    private String orderComment;
    private List<CartInfoVo> orderDetailList;
}
