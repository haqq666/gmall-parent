package com.atguigu.gmall.order.biz.impl;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuDetailFeign;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/13 19:35
 */
@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    SkuDetailFeign productFeignClient;
    @Autowired
    UserFeignClient userFeignClient;
    @Resource
    WareFeignClient wareFeignClient;
    @Autowired
    OrderInfoService orderInfoService;

    @Override
    public OrderConfirmDataVo getConfirmData() {
        OrderConfirmDataVo orderConfirmDataVo = new OrderConfirmDataVo();
        //获取购物车中选择的商品
        List<CartInfo> data = cartFeignClient.getChecked().getData();
        List<CartInfoVo> infoVoList = data.stream()
                .map(cartInfo -> {
                    CartInfoVo cartInfoVo = new CartInfoVo();
                    cartInfoVo.setSkuId(cartInfo.getSkuId());
                    cartInfoVo.setImgUrl(cartInfo.getImgUrl());
                    cartInfoVo.setSkuName(cartInfo.getSkuName());
                    //现查
                    cartInfoVo.setOrderPrice(productFeignClient.getSkuDetailTo1010price(cartInfo.getSkuId()).getData());
                    cartInfoVo.setSkuNum(cartInfo.getSkuNum());

                    String hasStock = wareFeignClient.hasStock(cartInfo.getSkuId(), cartInfo.getSkuNum());
                    cartInfoVo.setHasStock(hasStock);
                    return cartInfoVo;
                }).collect(Collectors.toList());

        orderConfirmDataVo.setDetailArrayList(infoVoList);
        //获取商品总数
        Integer integer = infoVoList.stream()
                .map(CartInfoVo::getSkuNum)
                .reduce(Integer::sum).get();
        orderConfirmDataVo.setTotalNum(integer);
        //获取商品总金额
        BigDecimal totalAmount = infoVoList.stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum() + "")))
                .reduce(BigDecimal::add).get();
        orderConfirmDataVo.setTotalAmount(totalAmount);

        //查询用户地址
        List<UserAddress> userAddresses = userFeignClient.getUserAddress().getData();
        orderConfirmDataVo.setUserAddressList(userAddresses);


        //生成tradeNo
        String tradeNo = generateTradeNo();
        orderConfirmDataVo.setTradeNo(tradeNo);

        return orderConfirmDataVo;
    }

    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo submitVo) {
        //验令牌
      Boolean check = checkTradeNo(tradeNo);
      if (!check){
          throw new GmallException(ResultCodeEnum.TOKEN_INVAILD);
      }
        //验库存
        List<String> noStockSkus = new ArrayList<>();
        for (CartInfoVo cartInfoVo : submitVo.getOrderDetailList()) {
            Long skuId = cartInfoVo.getSkuId();
            String stock = wareFeignClient.hasStock(skuId, cartInfoVo.getSkuNum());
            if (!"1".equals(stock)){
               noStockSkus.add(cartInfoVo.getSkuName());
            }
        }

        if (noStockSkus.size() > 0){
            String skuNames = noStockSkus.stream()
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .get();
            throw new GmallException(ResultCodeEnum.ORDER_NO_STOCK.getMessage() + skuNames,
                    ResultCodeEnum.ORDER_NO_STOCK.getCode());
        }

        //验价格
        ArrayList<String> skuNames = new ArrayList<>();
        for (CartInfoVo cartInfoVo : submitVo.getOrderDetailList()) {
            //查实时价格
            Result<BigDecimal> price = productFeignClient.getSkuDetailTo1010price(cartInfoVo.getSkuId());
            if (!price.getData().equals(cartInfoVo.getOrderPrice())){
                skuNames.add(cartInfoVo.getSkuName());
            }

            if (skuNames.size() > 0){
                String skuName = skuNames.stream().reduce((s1, s2) -> s1 + " " + s2).get();

                throw new GmallException(
                        ResultCodeEnum.ORDER_PRICE_CHANGE.getMessage() + skuName,
                        ResultCodeEnum.ORDER_PRICE_CHANGE.getCode());
            }
        }

        //保存到数据库
        Long orderId = orderInfoService.saveOrder(submitVo,tradeNo);

        //清空购物车里选中的商品
        cartFeignClient.deleteChecked();


        return orderId;
    }

    @Override
    public void closeOrder(Long orderId, Long userId) {
        List<ProcessStatus> expire = new ArrayList<>();
        expire.add(ProcessStatus.UNPAID);
        expire.add(ProcessStatus.FINISHED);
        orderInfoService.changeOrderStatus(
                orderId,userId,
                ProcessStatus.CLOSED,
                expire);
    }

    private Boolean checkTradeNo(String tradeNo) {
        String lua = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class),
                Arrays.asList(SysRedisConstant.ORDER_TEMP_TOKEN + tradeNo),
                 new String[]{"1"});

        if (execute > 0){
            return true;
        }
        return false;

    }

    /**
     * 生成tradeNo
     * @return
     */
    private String generateTradeNo() {
        long millis = System.currentTimeMillis();
        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();

        String tradeNo = millis + "_" + userId;

        //redis存一份
        redisTemplate.opsForValue()
                .set(SysRedisConstant.ORDER_TEMP_TOKEN + tradeNo,
                        "1",
                        15, TimeUnit.MINUTES);

        return tradeNo;
    }
}
