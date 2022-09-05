package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Mapper
* @createDate 2022-08-24 16:34:14
* @Entity com.atguigu.gmall.product.domain.SkuAttrValue
*/
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {

    List<SearchAttr> getAttrValueAndNAme(@Param("skuId") Long skuId);
}




