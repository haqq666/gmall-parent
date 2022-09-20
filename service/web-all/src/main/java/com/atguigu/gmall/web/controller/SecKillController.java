package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SecKillFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.vo.seckill.SeckillConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/19 23:46
 */
@Controller
public class SecKillController {

    @Autowired
    SecKillFeignClient secKillFeignClient;

    @GetMapping("/seckill.html")
    public String SecKillPage(Model model){
        Result<List<SeckillGoods>> secKillList = secKillFeignClient.getCurrentSecKillList();
        model.addAttribute("list",secKillList.getData());
        return "seckill/index";
    }
   @GetMapping("/seckill/{skuId}.html")
    public String seckillDetail(@PathVariable("skuId")Long skuId,Model model){
       Result<SeckillGoods> goods = secKillFeignClient.getSeckillDetail(skuId);
       model.addAttribute("item",goods.getData());
       return "seckill/item";
   }
   @GetMapping("/seckill/queue.html")
    public String secKillQueue(@RequestParam("skuId")Long skuId,
                               @RequestParam("skuIdStr")String skuIdStr,
                               Model model){
        model.addAttribute("skuId",skuId);
        model.addAttribute("skuIdStr",skuIdStr);
        return "seckill/queue";
   }

   @GetMapping("seckill/trade.html")
    public String seckillTrade(@RequestParam("skuId")Long skuId,
                               Model model){

       Result<SeckillConfirmVo> result = secKillFeignClient.SeckillConfirmVo(skuId);

       model.addAttribute("detailArrayList",result.getData().getTempOrder().getOrderDetailList());
        model.addAttribute("totalNum",result.getData().getTempOrder().getOrderDetailList().size());
        model.addAttribute("totalAmount",result.getData().getTotalAmount());
        model.addAttribute("userAddressList",result.getData().getUserAddressList());

       return "seckill/trade";
   }
}
