package com.atguigu.gmall.order;
import java.math.BigDecimal;
import java.util.List;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/12 22:54
 */
@SpringBootTest
public class ShardingTest {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Test
    public void testAdd(){
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setUserId(1L);
        orderInfo.setTotalAmount(new BigDecimal("2000.00"));

        orderInfoMapper.insert(orderInfo);

    }

    @Test
    public void testSelect(){
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",1L);
        List<OrderInfo> orderInfos = orderInfoMapper.selectList(wrapper);
        for (OrderInfo orderInfo : orderInfos) {
            System.out.println(orderInfo);
        }
    }
}
