package com.atguigu.gmall.cart.service.impl;

import java.math.BigDecimal;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.feign.product.SkuDetailFeign;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 19:32
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    SkuDetailFeign productFeign;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public SkuInfo addCarthtml(Long skuId, Integer skuNum) {
        //拿到缓存的key
        String cartKey = getCartKey();
        //添加到redis

        SkuInfo skuInfo = addCart(skuId, skuNum, cartKey);
        //设置临时用户的过期时间 90天
        UserAuthInfo info = AuthUtils.getCurrentAuthUserInfo();
        if (info.getUserId() == null){
            String tempKey = SysRedisConstant.CART_KEY + info.getUserTempId();
            redisTemplate.expire(tempKey,90, TimeUnit.DAYS);
        }
        return skuInfo;
    }

    @Override
    public SkuInfo addCart(Long skuId, Integer skuNum, String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        Boolean hasKey = cart.hasKey(skuId.toString());
        if (!hasKey) {
            if (cart.size() + 1 > SysRedisConstant.MAX_CART_NUMBER){
                throw new GmallException(ResultCodeEnum.CART_OVER_FLOW);
            }
            //调用product方法，获取用商品信息
            SkuInfo skuInfo = productFeign.getSkuDetailToInfo(skuId).getData();
            //将商品信息转化成cartInfo
            CartInfo cartInfo = converCartInfo2SkuInfo(skuInfo);
            cartInfo.setSkuNum(skuNum);
            //保存在redis中
            cart.put(skuId.toString(), Jsons.toStr(cartInfo));

            return skuInfo;
        } else {
            // String cartItem = cart.get(skuId);
            BigDecimal price = productFeign.getSkuDetailTo1010price(skuId).getData();
            CartInfo cartItem = getCartItemFromCart(skuId, cartKey);
            cartItem.setSkuNum(cartItem.getSkuNum() + skuNum);
            cartItem.setCartPrice(price);
            cartItem.setUpdateTime(new Date());
            //更新redis信息
            cart.put(skuId.toString(),Jsons.toStr(cartItem));
            //转化成skuIf
           return converSkuInfo2CartInfo(cartItem);
        }
    }

    private SkuInfo converSkuInfo2CartInfo(CartInfo cartItem) {
        SkuInfo skuInfo = new SkuInfo();

        skuInfo.setPrice(cartItem.getCartPrice());
        skuInfo.setSkuName(cartItem.getSkuName());
        skuInfo.setSkuDefaultImg(cartItem.getImgUrl());
        skuInfo.setId(cartItem.getSkuId());

        return skuInfo;
    }

    @Override
    public CartInfo getCartItemFromCart(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

        String cartItemStr = cart.get(skuId.toString());
        return Jsons.toObj(cartItemStr, CartInfo.class);
    }

    @Override
    public List<CartInfo> getCartList(String cartKey) {

        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

        List<CartInfo> cartInfos = cart.values().stream()
                .map(itemStr -> Jsons.toObj(itemStr, CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());

        return cartInfos;
    }

    @Override
    public void updateCart(Long skuId, Integer num, String cartKey) {
        //获取购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo = getCartItemFromCart(skuId, cartKey);
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuNum(cartInfo.getSkuNum() + num);
        //更新redis数据
        cart.put(skuId.toString(),Jsons.toStr(cartInfo));
    }

    @Override
    public void checkCart(Long skuId, Integer status, String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        CartInfo cartItem = getCartItemFromCart(skuId, cartKey);
        cartItem.setIsChecked(status);
        cartItem.setUpdateTime(new Date());
        cart.put(skuId.toString(),Jsons.toStr(cartItem));
    }

    @Override
    public void delete(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

         cart.delete(skuId.toString());
    }

    @Override
    public void MergeTempCartAndUserCart() {
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthUserInfo();
        //判断是否需要合并
        if (authInfo.getUserId() != null && !StringUtils.isEmpty(authInfo.getUserTempId())){

            String tempKey = SysRedisConstant.CART_KEY + authInfo.getUserTempId();
            List<CartInfo> cartList = getCartList(tempKey);
            if (cartList !=null && cartList.size() > 0){
                //合并
                String userKey = SysRedisConstant.CART_KEY + authInfo.getUserId();

                for (CartInfo cartInfo : cartList) {
                    addCart(cartInfo.getSkuId(),cartInfo.getSkuNum(),userKey);
                    //删除
                    delete(cartInfo.getSkuId(),tempKey);
                }
            }
        }







    }

    /**
     * 将skuInfo转化成为cartInfo
     *
     * @param skuInfo
     * @return
     */
    private CartInfo converCartInfo2SkuInfo(SkuInfo skuInfo) {

        CartInfo cartInfo = new CartInfo();

        cartInfo.setSkuId(skuInfo.getId());
        cartInfo.setCartPrice(skuInfo.getPrice());
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(skuInfo.getPrice());

        return cartInfo;
    }

    /**
     * 获取购物车的key
     * @return
     */
    @Override
    public String getCartKey() {
        String cartKey = "";
        UserAuthInfo userInfo = AuthUtils.getCurrentAuthUserInfo();
        if (userInfo.getUserId() != null) {
            cartKey = SysRedisConstant.CART_KEY + userInfo.getUserId();
        } else {
            cartKey = SysRedisConstant.CART_KEY + userInfo.getUserTempId();
        }
        return cartKey;
    }

}
