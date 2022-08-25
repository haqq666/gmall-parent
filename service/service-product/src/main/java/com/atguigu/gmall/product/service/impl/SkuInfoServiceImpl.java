package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-24 16:34:14
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Resource
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageService skuImageService;
    @Autowired
    SkuAttrValueService skuAttrValueService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //保存sku_info数据
        skuInfoMapper.insert(skuInfo);
        Long id = skuInfo.getId();
        //保存 sku_image 数据
        //List<SkuImage> skuImageList;
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(id);
        }
        skuImageService.saveBatch(skuImageList);

        //保存sku_attr_value
        //	List<SkuAttrValue> skuAttrValueList;
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(id);
        }
        skuAttrValueService.saveBatch(skuAttrValueList);


        //保存sku_sale_attr_value
        //List<SkuSaleAttrValue> skuSaleAttrValueList;
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(id);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
    }

    @Override
    public void cancelSale(Long skuId) {
        skuInfoMapper.UpdateIsSale(skuId,0); //1: 上架 0: 下架
        // TODO: es删除
    }

    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.UpdateIsSale(skuId,1); //1: 上架 0: 下架
        // TODO: es新增
    }
}




