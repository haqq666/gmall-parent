package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-24 16:34:14
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void cancelSale(Long skuId);

    void onSale(Long skuId);

    SkuDetailsTo getSkuDetailTo(Long skuId);

    SkuInfo getSkuDetailToInfo(Long skuId);

    CategoryViewTo getSkuDetailToCategoryTree(Long c3Id);

    List<SpuSaleAttr> getSpuDetailToSaleAttrList(Long spuId, Long skuId);

    String getSkuDetailToValuesSkuJson(Long spuId);

    BigDecimal getSkuDetailTo1010price(Long skuId);

    List<SkuImage> getSkuInfoImageList(Long skuId);

    List<Long> getAllSkuId();

}
