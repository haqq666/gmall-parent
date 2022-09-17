package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/17 0:26
 */
public interface AlipayService {

    String getAliPayPageHtml(Long orderId) throws AlipayApiException;

    boolean alrsaCheckV1(Map<String, String> payParam) throws AlipayApiException;
}
