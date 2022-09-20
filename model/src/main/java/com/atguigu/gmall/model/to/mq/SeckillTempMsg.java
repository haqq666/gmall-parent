package com.atguigu.gmall.model.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 20:46
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeckillTempMsg {
    public Long skuId;
    public Long userId;
    public String strCode;//秒杀码
}
