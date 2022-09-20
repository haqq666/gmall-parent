package com.atguigu.gmall.seckill.schedule;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 0:41
 */
@Slf4j
@Service
public class SecKillGoodsUpService {

    @Autowired
    SeckillGoodsCacheOpsService cacheOpsService;

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Scheduled(cron = "0 0 2 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void upSecKillGoods(){
        log.info("正在上传秒杀商品");
        //拿到要秒杀的数据
        List<SeckillGoods> list = seckillGoodsService.getCurrentSecKillList();

        //将秒杀的数据放到缓存中 redis + 本地缓存
        cacheOpsService.upSecKillGoods(list);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void clearSecKillGoods(){
        log.info("正在清理缓存");

        cacheOpsService.clearSecKillGoods();
    }

}
