package com.atguigu.gmall.model.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/14 18:57
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderMsg {
    private Long userId;
    private Long orderId;
}
