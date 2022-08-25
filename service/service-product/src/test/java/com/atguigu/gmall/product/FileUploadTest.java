package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 19:34
 */
//@SpringBootTest
public class FileUploadTest {

    @Test
    public void test() throws Exception {
        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        try {
            MinioClient minioClient = new MinioClient(
                    "http://192.168.200.100:9000",
                    "admin",
                    "admin123456");
            boolean isExist = minioClient.bucketExists("gmall");
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                minioClient.makeBucket("gmall");
            }
            FileInputStream inputStream = new FileInputStream("D:\\alicloud\\shopping\\尚品汇\\资料\\03 商品图片\\品牌\\pingguo.png");
            minioClient.putObject(
                    "gmall",
                    "pingguo.png",
                    inputStream,
                    new PutObjectOptions(inputStream.available(), -1L));
            String url = "http://192.168.200.100:9000/" + "gmall/" + "pingguo.png";
            System.out.println(url);
        } catch (InvalidEndpointException e) {
            System.out.println("发生错误" + e);
        }
    }
}
