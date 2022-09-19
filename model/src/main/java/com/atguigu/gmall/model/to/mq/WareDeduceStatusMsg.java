package com.atguigu.gmall.model.to.mq;

import lombok.Data;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/19 1:47
 */
@Data
public class WareDeduceStatusMsg {
    private Long orderId;
    private String status;
}
