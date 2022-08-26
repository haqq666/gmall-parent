package com.atguigu.gmall.model.to;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 20:59
 */
@Data
public class SkuDetailsTo {
    private SkuInfo skuInfo;
    private CategoryViewTo categoryView;
    private List<SpuSaleAttr> spuSaleAttrList;
    private String valuesSkuJson;
    private BigDecimal price;
}
