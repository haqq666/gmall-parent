package com.atguigu.gmall.model.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/19 0:50
 */
@Data
public class WareDeduceMsg {

    private Long orderId;
    private String consignee;
    private String consigneeTel;
    private String orderComment;
    private String orderBody;
    private String deliveryAddress;
    private String paymentWay = "2";
    private List<StoreDeduceSkuInfo> details;


}
