package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.config.minio.MinioAutoConfiguration;
import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 18:43
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    MinioProperties minioProperties;
    @Autowired
    MinioAutoConfiguration minioAutoConfiguration;

    @Override
    public String fileUpload(MultipartFile file) throws Exception {
        // 检查存储桶是否已经存在

        String filename = UUID.randomUUID().toString().replace("-","") + "_" + file.getOriginalFilename();
        String data = DateUtil.formatDate(new Date());

        PutObjectOptions options = new PutObjectOptions(file.getSize(), -1);
        options.setContentType(file.getContentType());
        // 使用putObject上传一个文件到存储桶中。
        minioAutoConfiguration.getMinioClient().putObject(
                minioProperties.getBucketName(),
                data +"/"+ filename,
                file.getInputStream(),
                options);
        String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName()  + "/" + data + "/" + filename;

        return url;
    }
}
