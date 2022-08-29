package com.atguigu.gmall.item;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/30 1:12
 */
public class BloomFilterTest {

    @Test
    public void test(){
        //创建
        BloomFilter<Long> filter = BloomFilter.create(Funnels.longFunnel(),100,0.0001);

//        BloomFilter<Long> filter = BloomFilter.create((from, into) -> {
//            into.putLong(Long.parseLong(from.toString()));
//        }, 10000, 0.0001);
        //放值

        for (long i = 0; i < 20; i++) {
            filter.put(i);
        }


        //判断
        System.out.println(filter.mightContain(1L)); //T
        System.out.println(filter.mightContain(15L));//T
        System.out.println(filter.mightContain(20L));//F
        System.out.println(filter.mightContain(99L));//F

    }
}
