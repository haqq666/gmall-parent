package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/19 8:57
 */
@Data
public class WareChildOrderVo {
    private Long orderId;
    private String consignee;
    private String  consigneeTel;
    private String orderComment;
    private String orderBody;
    private String deliveryAddress;
    private String  paymentWay;
    private String  wareId;
    private List<WareChildOrderDetailItemVo> details;




}
