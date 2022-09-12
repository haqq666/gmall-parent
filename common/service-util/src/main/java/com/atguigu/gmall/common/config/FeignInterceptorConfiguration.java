package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/12 21:38
 */
public class FeignInterceptorConfiguration {

    @Bean
    public RequestInterceptor userHeaderInterceptor() {

        return (template) -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String userId = request.getHeader(SysRedisConstant.USER_HANDER);

            String userTempId = request.getHeader(SysRedisConstant.USER_TEMP_HANDER);
            template.header(SysRedisConstant.USER_HANDER, userId);
            template.header(SysRedisConstant.USER_TEMP_HANDER, userTempId);
        };
    }
}
