package com.atguigu.gmall.product.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 20:02
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.minio")
public class MinioProperties {
    String endpoint;
    String ak;
    String sk;
    String bucketName;
}
