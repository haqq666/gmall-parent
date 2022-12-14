package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.SkuValueJson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2022-08-24 16:34:13
* @Entity com.atguigu.gmall.product.domain.SpuSaleAttr
*/
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    List<SpuSaleAttr> spuSaleAttrList(@Param("spuId") Long spuId);

    List<SpuSaleAttr> spuSaleAttrMapper(@Param("spuId") Long spuId, @Param("skuId") Long skuId);

    List<SkuValueJson> getAllSkuValuesSkuJson(@Param("spuId") Long spuId);
}




