package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.cache.annontation.GmallCache;
import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
* @createDate 2022-08-23 18:40:41
*/
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
    implements BaseCategory3Service{

    @Resource
    BaseCategory3Mapper baseCategory3Mapper;

    @Override
    public List<BaseCategory3> getCategory2Child(Long c2Id) {

        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id",c2Id);
        List<BaseCategory3> list = baseCategory3Mapper.selectList(wrapper);

        return list;
    }

    @GmallCache(cacheKey = SysRedisConstant.CATEGORY_KEY)
    @Override
    public List<CategoryTreeTo> getCategoryTreeTo() {

        return baseCategory3Mapper.getCategoryTreeTo();
    }

}




