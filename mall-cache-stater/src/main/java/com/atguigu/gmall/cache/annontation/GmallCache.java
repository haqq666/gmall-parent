package com.atguigu.gmall.cache.annontation;

import java.lang.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 20:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface GmallCache {

    String cacheKey() default "";
    String bloomName() default "";
    String bloomValue() default "";
    String lockName() default "lock:global:";
    long ttl() default 60*30L;
}
