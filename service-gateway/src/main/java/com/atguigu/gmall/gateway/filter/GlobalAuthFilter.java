package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.AuthUrlProperties;
import com.atguigu.gmall.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/7 8:41
 */
@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    AuthUrlProperties authUrlProperties;

    AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //拿到请求路径
        String path = exchange.getRequest().getURI().getPath();
        log.info("{} 请求开始", path);
        String uri = exchange.getRequest().getPath().toString();
        log.info("uri --- {} 请求开始", uri);

        //静态资源直接访问
        for (String url : authUrlProperties.getNoAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match) {
                return chain.filter(exchange);
            }
        }

        //api/inner 拒绝
        for (String url : authUrlProperties.getDenyUrl()) {
            boolean match = matcher.match(url, path);
            if (match) {
                Result<String> build = Result.build("", ResultCodeEnum.PERMISSION);
                return responseResult(build, exchange);
            }
        }

        //验证要登录的页面是否带token，token是否合法
        for (String url : authUrlProperties.getLoginAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match) {
                //查token
                String tokenValue = getTokenValue(exchange);
                //获取userInfo的信息
                UserInfo userInfo = getTokenUserInfo(tokenValue);
                if (userInfo != null) {
                    //id穿透
                    ServerWebExchange webExchange = userIdTransport(userInfo, exchange);
                    return chain.filter(webExchange);
                } else {
                    //打回登录页面
                    return redirectToCustomPage(authUrlProperties.getLoginPage() + "?originUrl=" + uri, exchange);
                }
            }
        }


        //普通请求 若携带token 检验token //若未携带token就放行
        String tokenValue = getTokenValue(exchange);
        UserInfo userInfo = getTokenUserInfo(tokenValue);

        if (tokenValue != null && userInfo == null){
            return redirectToCustomPage(authUrlProperties.getLoginPage() + "?originUrl=" + uri, exchange);
        }

        exchange = userIdTransport(userInfo, exchange);

        //deleteUserInfo(exchange);
        return chain.filter(exchange);
    }

    //清除userInfo
    private Mono<Void> deleteUserInfo(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String path = exchange.getRequest().getPath().toString();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().add(HttpHeaders.LOCATION, path);

        //清除老cookie
        ResponseCookie cookie = ResponseCookie.from("userInfo", "777")
                .maxAge(0)
                .domain(".gmall.com")
                .path("/")
                .build();
        response.getCookies().set("userInfo", cookie);

        return response.setComplete();
    }

    private ServerWebExchange userIdTransport(UserInfo userInfo, ServerWebExchange exchange) {
        String userTempId = getUserTempId(exchange);
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        if (userInfo != null) {
            mutate.header(SysRedisConstant.USER_HANDER, userInfo.getId().toString()).build();
        }
        if (!StringUtils.isEmpty(userTempId)){
            mutate.header(SysRedisConstant.USER_TEMP_HANDER,userTempId).build();
        }
            return exchange.mutate()
                    .request(request)
                    .response(exchange.getResponse())
                    .build();
    }

    private String getUserTempId(ServerWebExchange exchange) {
        HttpCookie cookieToken = exchange.getRequest().getCookies().getFirst(SysRedisConstant.USER_TEMP_HANDER);
        if (cookieToken != null) {
            return cookieToken.getValue();
        }
        //存在token中
        return exchange.getRequest().getHeaders().getFirst(SysRedisConstant.USER_TEMP_HANDER);
    }

    private Mono<Void> redirectToCustomPage(String location, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().add(HttpHeaders.LOCATION, location);

        //清除老cookie
        ResponseCookie cookie = ResponseCookie.from("token", "777")
                .maxAge(0)
                .domain(".gmall.com")
                .path("/")
                .build();
        response.getCookies().set("token", cookie);

        cookie = ResponseCookie.from("userInfo", "777")
                .maxAge(0)
                .domain(".gmall.com")
                .path("/")
                .build();
        response.getCookies().set("userInfo", cookie);

        return response.setComplete();
    }

    private UserInfo getTokenUserInfo(String tokenValue) {
        String json = stringRedisTemplate.opsForValue().get(SysRedisConstant.LOGIN_USER_TOKEN + tokenValue);
        if (json != null) {
            return Jsons.toObj(json, UserInfo.class);
        }
        return null;
    }

    private String getTokenValue(ServerWebExchange exchange) {

        HttpCookie cookieToken = exchange.getRequest().getCookies().getFirst("token");
        if (cookieToken != null) {
            return cookieToken.getValue();
        }
        //存在token中
        return exchange.getRequest().getHeaders().getFirst("token");
    }

    private Mono<Void> responseResult(Result<String> build, ServerWebExchange exchange) {

        ServerHttpResponse response = exchange.getResponse();
        String result = Jsons.toStr(build);
        DataBuffer buffer = response.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));

        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(buffer));
    }


}
