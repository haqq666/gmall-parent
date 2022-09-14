package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 19:25
 */
@Data
public class CartInfoVo {
    private Long skuId;
    private String imgUrl;
    private String skuName;
    private BigDecimal orderPrice;
    private Integer skuNum;
    private String hasStock = "1";
}
