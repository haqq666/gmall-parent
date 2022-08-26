package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    @Resource
    BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

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

    @Override
    public SkuDetailsTo getSkuDetailTo(Long skuId) {
        SkuDetailsTo skuDetailsTo = new SkuDetailsTo();


        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);


        Long category3Id = skuInfo.getCategory3Id();
        //查询categoryView
        CategoryViewTo categoryViewTo = baseCategory3Mapper.selectCategoryView(category3Id);
        skuDetailsTo.setCategoryView(categoryViewTo);

        //skuInfo
        //	List<SkuImage> skuImageList;
        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<SkuImage> imageList = skuImageService.list(wrapper);
        skuInfo.setSkuImageList(imageList);

        //	List<SkuAttrValue> skuAttrValueList;
        QueryWrapper<SkuAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        List<SkuAttrValue> skuAttrValues = skuAttrValueService.list(queryWrapper);
        skuInfo.setSkuAttrValueList(skuAttrValues);

        //	List<SkuSaleAttrValue> skuSaleAttrValueList;
        QueryWrapper<SkuSaleAttrValue> SaleAttrQueryWrapper = new QueryWrapper<>();
        SaleAttrQueryWrapper.eq("sku_id",skuId);
        List<SkuSaleAttrValue> SaleAttrList = skuSaleAttrValueService.list(SaleAttrQueryWrapper);
//        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueService.getSkuSaleAttrValueList(skuId);
        skuInfo.setSkuSaleAttrValueList(SaleAttrList);

        skuDetailsTo.setSkuInfo(skuInfo);

        //price
        BigDecimal price = skuInfoMapper.get1010price(skuId);
        skuDetailsTo.setPrice(price);

        //List<SpuSaleAttr> spuSaleAttrList;
//        QueryWrapper<SpuSaleAttr> spuSaleAttrQueryWrapper = new QueryWrapper<>();
//        spuSaleAttrQueryWrapper.eq("spu_id",skuInfo.getSpuId());
//        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.list(spuSaleAttrQueryWrapper);
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.getSpuSaleAttrList(spuId,skuId);
        skuDetailsTo.setSpuSaleAttrList(spuSaleAttrList);


        return skuDetailsTo;
    }
}




