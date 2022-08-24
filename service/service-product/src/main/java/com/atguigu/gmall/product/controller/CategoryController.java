package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
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
    @Autowired
    BaseCategory3Service baseCategory3Service;

    /**
     * 获取所有商品的一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result method(){
        List<BaseCategory1> list = baseCategory1Service.list();
        return Result.ok(list);
    }

    /**
     * 根据一级分类id查找二级分类
     * @param c1Id 一级分类的id
     * @return
     */
    @GetMapping("/getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable("c1Id")Long c1Id){
       List<BaseCategory2> list =  baseCategory2Service.getCategory1Child(c1Id);
       return Result.ok(list);
    }

    /**
     * 根据二级分类id查询三级分类
     * @param c2Id
     * @return
     */
    @GetMapping("getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable("c2Id")Long c2Id){
        List<BaseCategory3> list =  baseCategory3Service.getCategory2Child(c2Id);
        return Result.ok(list);
    }
}
