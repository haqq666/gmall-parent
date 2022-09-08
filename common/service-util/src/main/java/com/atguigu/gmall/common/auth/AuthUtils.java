package com.atguigu.gmall.common.auth;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 19:16
 */
public class AuthUtils {

    public static UserAuthInfo getCurrentAuthUserInfo(){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader(SysRedisConstant.USER_HANDER);
        UserAuthInfo userAuthInfo = new UserAuthInfo();

        if (userId != null){
            userAuthInfo.setUserId(Long.parseLong(userId));
        }

        String userTempId = request.getHeader(SysRedisConstant.USER_TEMP_HANDER);
        userAuthInfo.setUserTempId(userTempId);

        return userAuthInfo;
    }
}
