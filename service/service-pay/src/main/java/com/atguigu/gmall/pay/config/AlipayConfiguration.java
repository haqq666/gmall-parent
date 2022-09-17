package com.atguigu.gmall.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/17 0:02
 */
@Configuration
public class AlipayConfiguration {

    @Bean
    public AlipayClient alipayClient(AliPayConfigurationProperties properties){
        AlipayClient alipayClient = new DefaultAlipayClient(
                properties.getGatewayUrl(),
                properties.getAppId(),
                properties.getMerchantPrivateKey(),
                "json",
                properties.getCharset(),
                properties.getAlipayPublicKey(),
                properties.getSignType());

        return alipayClient;
    }
}
