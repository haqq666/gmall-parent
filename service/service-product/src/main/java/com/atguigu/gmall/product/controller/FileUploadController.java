package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/24 23:42
 */
@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    @Autowired
    FileUploadService fileUploadService;

    @PostMapping("/fileUpload")
    public Result  fileUpload(@RequestPart MultipartFile file) throws Exception {
        String utl = fileUploadService.fileUpload(file);
        return Result.ok(utl);
    }

}
