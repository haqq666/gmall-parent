package com.atguigu.gmall.order;

import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/15 10:19
 */
@SpringBootTest
public class CloseOrderTest {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Test
    public void test01(){
        List<ProcessStatus> list = new ArrayList<>();
        list.add(ProcessStatus.UNPAID);
        list.add(ProcessStatus.FINISHED);
        orderInfoMapper.closeOrder(777117268201963520L,2L, OrderStatus.CLOSED, ProcessStatus.CLOSED,list);
    }

}
