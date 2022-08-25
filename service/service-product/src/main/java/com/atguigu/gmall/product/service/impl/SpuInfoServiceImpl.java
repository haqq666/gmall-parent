package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2022-08-24 16:34:14
*/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService{

    @Resource
    SpuInfoMapper spuInfoMapper;
    @Resource
    SpuImageService spuImageService;
    @Resource
    SpuSaleAttrService spuSaleAttrService;
    @Resource
    SpuSaleAttrValueService spuSaleAttrValueService;

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //存入spuInfo
        spuInfoMapper.insert(spuInfo);
        Long id = spuInfo.getId();
        //存入图像
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(id);
        }
        spuImageService.saveBatch(spuImageList);
        //存入销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(id);
            //销售属性的值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(id);
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
            }
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList);
        }
        spuSaleAttrService.saveBatch(spuSaleAttrList);
    }
}




