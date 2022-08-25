package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 22:33
 */
@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 分页查询所有的sku
     * @param pn
     * @param ps
     * @return
     */
    @GetMapping("/list/{pn}/{ps}")
    public Result list(@PathVariable("pn")Long pn,
                       @PathVariable("ps")Long ps){
        Page<SkuInfo> skuInfoPage = new Page<>(pn, ps);
        Page<SkuInfo> page = skuInfoService.page(skuInfoPage);
        return Result.ok(page);
    }

    /**
     * 保存skuInfo信息
     * @param skuInfo SkuInfo详细信息
     * @return 成功
     */
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId")Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }

}
