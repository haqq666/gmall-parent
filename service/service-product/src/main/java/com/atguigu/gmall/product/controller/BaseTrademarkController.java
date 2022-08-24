package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/24 22:52
 */
@RestController
@RequestMapping("/admin/product")
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * 分页查找品牌
     * @param num 当前页
     * @param size 每页显示条数
     */
    @GetMapping("baseTrademark/{num}/{size}")
    public Result baseTrademark(@PathVariable("num")Long num,
                                @PathVariable("size")Long size){

        Page<BaseTrademark> baseTrademarkPage = new Page<>(num,size);
        Page page = baseTrademarkService.page(baseTrademarkPage);
        return Result.ok(page);
    }

    /**
     * 根据id查找品牌信息
     * @param id 品牌id
     * @return
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getById(@PathVariable("id")Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    /**
     * 修改的品牌信息的保存
     * @param baseTrademark 品牌信息
     */
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    @PostMapping("baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @DeleteMapping("baseTrademark/remove/{id}")
    public Result deleteBaseTrademarkById(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }



}
