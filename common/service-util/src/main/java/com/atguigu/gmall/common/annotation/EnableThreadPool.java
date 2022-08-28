package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.ThreadPool.ThreadPoolAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/29 0:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(ThreadPoolAutoConfiguration.class)
public @interface EnableThreadPool {
}
