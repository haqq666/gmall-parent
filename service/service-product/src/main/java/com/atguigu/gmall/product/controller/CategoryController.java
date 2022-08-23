package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/23 17:39
 */
@RestController
@RequestMapping("admin/product")
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;
    @Autowired
    BaseCategory2Service baseCategory2Service;

    /**
     * 获取所有商品的一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result method(){
        List<BaseCategory1> list = baseCategory1Service.list();
        return Result.ok(list);
    }
    @GetMapping("/getCategory2/{c1Id}")
    public Result method(@PathVariable("c1Id")Long c1Id){
       List<BaseCategory2> list =  baseCategory2Service.getCategory1Child(c1Id);
       return Result.ok(list);
    }

}
