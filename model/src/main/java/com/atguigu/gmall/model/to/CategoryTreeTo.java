package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 18:22
 */
@Data
public class CategoryTreeTo {
    private Long categoryId;
    private String categoryName;
    private List<CategoryTreeTo> categoryChild;
}
