package com.atguigu.gmall.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/17 0:08
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.alipay")
public class AliPayConfigurationProperties {
   private String gatewayUrl;
    private String appId;
    private String merchantPrivateKey;
    private String charset;
    private String alipayPublicKey;
    private String signType;
    private String notifyUrl;
    private String returnUrl;

}
