package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 20:25
 */
@RestController
@RequestMapping("/admin/product")
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuImageService spuImageService;

    /**
     * 分页查询spu信息
     * @param pn 页数
     * @param ps 每页显示条数
     * @param category3Id 三级分类id
     */
    @GetMapping("{pn}/{ps}")
    public Result getSpuInfo(@PathVariable("pn") Long pn,
                             @PathVariable("ps") Long ps,
                             @RequestParam("category3Id") Long category3Id){
        Page<SpuInfo> page = new Page<>(pn,ps);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        Page<SpuInfo> result = spuInfoService.page(page, wrapper);
        return Result.ok(result);
    }

    /**
     * 保存spu
     * @param spuInfo
     * @return
     */
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        spuInfoService.saveSpuInfo(spuInfo);

        return Result.ok();
    }

    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId")Long spuId){
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> list = spuImageService.list(wrapper);
        return Result.ok(list);
    }
}
