package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 18:39
 */
@RestController("/admin/product")
public class BloomOpsController {

    @Autowired
    BloomOpsService bloomOpsService;
    @Autowired
    BloomDataQueryService bloomDataQueryService;

    @GetMapping("rebuild/now")
    public Result rebuild(){
       String bloomName = SysRedisConstant.BlOOM_SKUID;
        bloomOpsService.rebuild(bloomName,bloomDataQueryService);
        return Result.ok();
    }

}
