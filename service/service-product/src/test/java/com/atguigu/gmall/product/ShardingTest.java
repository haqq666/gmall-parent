package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/2 19:49
 */
@SpringBootTest
public class ShardingTest {

    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Test
    public void testrw(){
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark);

        baseTrademark.setTmName("小米");
        baseTrademarkMapper.updateById(baseTrademark);
        BaseTrademark baseTrademark1 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark1);

        //强制走主库
        HintManager.getInstance().setWriteRouteOnly();
        BaseTrademark baseTrademark2 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark2);
    }

    @Test
    public void testr(){
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark);

        BaseTrademark baseTrademark1 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark1);

        BaseTrademark baseTrademark2 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark2);

        BaseTrademark baseTrademark3 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark3);
    }

}
