package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/24 23:03
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor interceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //分页的配置
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true); //页码溢出后显示最后一页
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;

    }
}
