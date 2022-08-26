package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
* @author 乔豆麻担
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2022-08-24 16:34:14
* @Entity com.atguigu.gmall.product.domain.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void UpdateIsSale(@Param("skuId") Long skuId, @Param("sale") int sale);

    BigDecimal get1010price(@Param("skuId") Long skuId);
}




