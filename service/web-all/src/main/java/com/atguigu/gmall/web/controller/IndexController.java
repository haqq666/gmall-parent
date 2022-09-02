package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuDetailFeign;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/26 18:15
 */
@Controller
public class IndexController {

   @Autowired
   SkuDetailFeign categoryFeignClient;

   @GetMapping({"/", "/index"})
   public String method(Model model){
      Result<List<CategoryTreeTo>> result = categoryFeignClient.getCategoryTreeTo();
      if (result.isOk()){
         //成功
         model.addAttribute("list",result.getData());
      }
      return "index/index";
   }
}
