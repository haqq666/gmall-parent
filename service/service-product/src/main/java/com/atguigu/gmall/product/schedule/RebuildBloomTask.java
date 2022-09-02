package com.atguigu.gmall.product.schedule;


import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 19:08
 */
@Service
public class RebuildBloomTask {

    @Autowired
    BloomOpsService bloomOpsService;
    @Autowired
    BloomDataQueryService bloomDataQueryService;

    @Scheduled(cron = "0 0 3 ? * 3")
    public void rebuildBloomTask(){
        bloomOpsService.rebuild(SysRedisConstant.BlOOM_SKUID,bloomDataQueryService);
    }

}
