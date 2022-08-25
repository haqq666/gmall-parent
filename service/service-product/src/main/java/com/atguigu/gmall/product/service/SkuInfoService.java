package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 乔豆麻担
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-24 16:34:14
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void cancelSale(Long skuId);

    void onSale(Long skuId);
}