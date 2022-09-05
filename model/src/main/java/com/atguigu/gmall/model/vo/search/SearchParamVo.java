package com.atguigu.gmall.model.vo.search;

import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 21:00
 */
@Data
public class SearchParamVo {
    private Long category1Id;
    private Long category2Id;
    private Long category3Id;

    private String keyword;
    private String trademark;

    private List<String> props;

    private String order = "1:desc";
    private Integer pageNo = 1;


}
