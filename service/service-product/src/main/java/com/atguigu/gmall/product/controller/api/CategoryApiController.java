package com.atguigu.gmall.product.controller.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 18:19
 */
@RestController
@RequestMapping("api/inner/rpc/product")
public class CategoryApiController {

    @Autowired
    BaseCategory3Service baseCategory3Service;

    @GetMapping("/category/tree")
    public Result<List<CategoryTreeTo>> getCategoryTreeTo(){
        List<CategoryTreeTo> categoryTreeToList =  baseCategory3Service.getCategoryTreeTo();
        return Result.ok(categoryTreeToList);
    }

}
