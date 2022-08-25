package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/24 16:30
 */
@RequestMapping("admin/product")
@RestController
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 根据 分类id 查询 平台属性的信息
     * @param category1Id 一级分类id
     * @param category2Id 二级分类id
     * @param category3Id 三级分类id
     * @return 查询到平台属性的信息的列表
     */
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoAndValueByCategoryId(@PathVariable("category1Id")Long category1Id,
                               @PathVariable("category2Id")Long category2Id,
                               @PathVariable("category3Id")Long category3Id){
        List<BaseAttrInfo> list =  baseAttrInfoService.attrInfoAndValueByCategoryId(category1Id,category2Id,category3Id);
        System.out.println(list);
        return Result.ok(list);
    }

    /**
     * 保存或者修改属性
     * @param baseAttrInfo 属性的详细内容
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId")Long attrId){
        List<BaseAttrValue> list = baseAttrValueService.getAttrValueListByAttrId(attrId);
        return Result.ok(list);
    }

}
