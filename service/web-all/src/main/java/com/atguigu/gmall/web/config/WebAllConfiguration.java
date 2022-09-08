package com.atguigu.gmall.web.config;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 0:15
 */
@Configuration
public class WebAllConfiguration {
    @Bean
    public RequestInterceptor userHeaderInterceptor(){
        return (template) ->{
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String userId = request.getHeader(SysRedisConstant.USER_HANDER);

            template.header(SysRedisConstant.USER_HANDER,userId);
        };
    }
}
