package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2022-08-23 18:40:41
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    List<BaseCategory3> getCategory2Child(Long c2Id);

    List<CategoryTreeTo> getCategoryTreeTo();
}
