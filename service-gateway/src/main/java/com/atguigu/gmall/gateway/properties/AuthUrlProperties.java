package com.atguigu.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/7 18:51
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.auth")
public class AuthUrlProperties {

    private List<String> noAuthUrl;
    private List<String> loginAuthUrl;
    private List<String> denyUrl;
    private String loginPage;
}
