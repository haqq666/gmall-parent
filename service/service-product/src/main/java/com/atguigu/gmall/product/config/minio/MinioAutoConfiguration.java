package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 20:05
 */
@Configuration
public class MinioAutoConfiguration {

    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient getMinioClient() throws Exception {
        MinioClient minioClient = new MinioClient(
                "http://192.168.200.100:9000",
                "admin",
                "admin123456");

        // 检查存储桶是否已经存在
        boolean isExist = minioClient.bucketExists(minioProperties.getBucketName());
        if (!isExist){
            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        return minioClient;
    }
}
