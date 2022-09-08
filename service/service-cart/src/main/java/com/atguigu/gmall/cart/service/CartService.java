package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/8 19:31
 */

public interface CartService {
    SkuInfo addCarthtml(Long skuId, Integer skuNum);

     SkuInfo addCart(Long skuId, Integer skuNum, String cartKey);

     String getCartKey();

     CartInfo getCartItemFromCart(Long skuId, String cartKey);

    List<CartInfo> getCartList(String cartKey);

    void updateCart(Long skuId, Integer num, String cartKey);

    void checkCart(Long skuId, Integer status, String cartKey);

    void delete(Long skuId, String cartKey);

    void MergeTempCartAndUserCart();

}
