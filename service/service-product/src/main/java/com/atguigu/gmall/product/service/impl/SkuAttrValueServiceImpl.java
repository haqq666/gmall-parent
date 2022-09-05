package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service实现
* @createDate 2022-08-24 16:34:14
*/
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
    implements SkuAttrValueService{

    @Resource
    SkuAttrValueMapper skuAttrValueMapper;

    @Override
    public List<SearchAttr> getAttrValueAndNAme(Long skuId) {

        return skuAttrValueMapper.getAttrValueAndNAme(skuId);
    }
}




