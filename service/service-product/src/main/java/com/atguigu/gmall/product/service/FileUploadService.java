package com.atguigu.gmall.product.service;

import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/25 18:42
 */
public interface FileUploadService {
    String fileUpload(MultipartFile file) throws Exception;
}
