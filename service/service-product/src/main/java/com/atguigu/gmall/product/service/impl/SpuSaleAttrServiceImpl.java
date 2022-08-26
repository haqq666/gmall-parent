package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-24 16:34:13
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    @Resource
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {

        List<SpuSaleAttr> list = spuSaleAttrMapper.spuSaleAttrList(spuId);
        return list;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId, Long skuId) {
        return  spuSaleAttrMapper.spuSaleAttrMapper(spuId,skuId);
    }
}




