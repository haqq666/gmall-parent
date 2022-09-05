package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 20:53
 */
@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    @GetMapping("/list.html")
    public String search(SearchParamVo searchParamVo, Model model, HttpServletRequest httpServletRequest){

        Result<SearchResponseVo> result = searchFeignClient.search(searchParamVo);
        SearchResponseVo data = result.getData();

        //传来的值
        model.addAttribute("searchParam",data.getSearchParamVo());
        //品牌面包屑
        model.addAttribute("trademarkParam",data.getTrademarkParam());
        //属性面包屑（LIST prop:attrName,attrValue,attrId）
        model.addAttribute("propsParamList",data.getPropsParamList());
        //品牌列表（trademark：tmId，tmName tmLogoUrl）
        model.addAttribute("trademarkList",data.getTrademarkList());
        //属性列表
        model.addAttribute("attrsList",data.getAttrsList());
        //排序方式
        model.addAttribute("orderMap",data.getOrderMap());
        //商品列表
        model.addAttribute("goodsList",data.getGoodsList());
        //页码
        model.addAttribute("pageNo",data.getPageNo());
        //总页数
        model.addAttribute("totalPages",data.getTotalPages());
        //当前的url信息
        model.addAttribute("urlParam",data.getUrlParam());

        return "list/index";
    }
}
