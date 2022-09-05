package com.atguigu.gmall.model.vo.search;

import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 21:54
 */
@Data
public class AttrVo {
    private Long attrId;
    private String attrName;
    private List<String> attrValueList;
}
