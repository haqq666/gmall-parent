package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.FeignInterceptorConfiguration;
import com.atguigu.gmall.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/12 21:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(FeignInterceptorConfiguration.class)
public @interface EnableFeignInterceptorConfiguration {

}
