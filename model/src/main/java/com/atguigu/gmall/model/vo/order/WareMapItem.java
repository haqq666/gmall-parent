package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/19 2:41
 */
@Data
public class WareMapItem {
    //{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
    private Long wareId;
    private List<Long> skuIds;
}
