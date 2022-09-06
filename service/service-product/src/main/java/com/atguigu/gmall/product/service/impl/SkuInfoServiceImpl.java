package com.atguigu.gmall.product.service.impl;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.google.common.collect.Lists;
import java.util.Date;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailsTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    BaseTrademarkService baseTrademarkService;
    @Autowired
    BaseCategory2Service baseCategory2Service;
    @Autowired
    BaseCategory1Service baseCategory1Service;
    @Autowired
    SearchFeignClient searchFeignClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


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

        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(SysRedisConstant.BlOOM_SKUID);
        bloomFilter.add(id);

    }

    @Override
    public void cancelSale(Long skuId) {
        skuInfoMapper.UpdateIsSale(skuId,0); //1: 上架 0: 下架
        // es删除
        searchFeignClient.deleteGoods(skuId);
    }

    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.UpdateIsSale(skuId,1); //1: 上架 0: 下架
        //es 新增
        Goods goods = getGoodsBySkuId(skuId);
        searchFeignClient.saveGoods(goods);

    }

    /**
     * 查询SkuDetails 信息，将
     * @param skuId
     * @return
     */
    @Deprecated
    @Override
    public SkuDetailsTo getSkuDetailTo(Long skuId) {

        SkuDetailsTo skuDetailsTo = new SkuDetailsTo();


        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        skuDetailsTo.setSkuInfo(skuInfo);


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

        //price
        BigDecimal price = skuInfoMapper.get1010price(skuId);
        skuDetailsTo.setPrice(price);

        //private List<SpuSaleAttr> spuSaleAttrList;
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.getSpuSaleAttrList(spuId,skuId);
        skuDetailsTo.setSpuSaleAttrList(spuSaleAttrList);

        //private String valuesSkuJson;
        String valuesSkuJson =  spuSaleAttrService.getAllSkuValuesSkuJson(spuId);
        skuDetailsTo.setValuesSkuJson(valuesSkuJson);

        return skuDetailsTo;
    }

    @Override
    public SkuInfo getSkuDetailToInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    @Override
    public CategoryViewTo getSkuDetailToCategoryTree(Long c3Id) {
        CategoryViewTo categoryViewTo = baseCategory3Mapper.selectCategoryView(c3Id);
        return categoryViewTo;
    }

    @Override
    public List<SpuSaleAttr> getSpuDetailToSaleAttrList(Long spuId, Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.getSpuSaleAttrList(spuId,skuId);
        return spuSaleAttrList;
    }

    @Override
    public String getSkuDetailToValuesSkuJson(Long spuId) {
        String valuesSkuJson =  spuSaleAttrService.getAllSkuValuesSkuJson(spuId);
        return valuesSkuJson;
    }

    @Override
    public BigDecimal getSkuDetailTo1010price(Long skuId) {
        BigDecimal price = skuInfoMapper.get1010price(skuId);
        return price;
    }

    @Override
    public List<SkuImage> getSkuInfoImageList(Long skuId) {
        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<SkuImage> images = skuImageService.list(wrapper);
        return images;
    }

    @Override
    public List<Long> getAllSkuId() {
         return skuInfoMapper.getAllSkuId();
    }

    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        Goods goods = new Goods();
        goods.setId(skuId);

        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());

        BaseTrademark trademark = baseTrademarkService.getById(skuInfo.getTmId());
        goods.setTmId(trademark.getId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());

        BaseCategory3 baseCategory3 = baseCategory3Mapper.selectById(skuInfo.getCategory3Id());
        BaseCategory2 category2 = baseCategory2Service.getById(baseCategory3.getCategory2Id());
        BaseCategory1 category1 = baseCategory1Service.getById(category2.getCategory1Id());
        goods.setCategory1Id(category1.getId());
        goods.setCategory1Name(category1.getName());
        goods.setCategory2Id(category2.getId());
        goods.setCategory2Name(category2.getName());

        goods.setCategory3Id(baseCategory3.getId());
        goods.setCategory3Name(baseCategory3.getName());

        //TODO 热度查询
        
        goods.setHotScore(0L);


        List<SearchAttr> attrs = skuAttrValueService.getAttrValueAndNAme(skuId);
        goods.setAttrs(attrs);

        return goods;
    }
}




