package com.atguigu.gmall.model.to.mq;

import lombok.Data;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/19 0:53
 */
@Data
public class StoreDeduceSkuInfo {
    private Long skuId;
    private  Integer skuNum;
    private String skuName;
}
