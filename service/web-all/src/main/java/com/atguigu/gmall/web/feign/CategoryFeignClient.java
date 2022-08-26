package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 20:05
 */
@RequestMapping("api/inner/rpc/product")
@FeignClient("service-product")
public interface CategoryFeignClient {

    @GetMapping("/category/tree")
    Result<List<CategoryTreeTo>> getCategoryTreeTo();
}
