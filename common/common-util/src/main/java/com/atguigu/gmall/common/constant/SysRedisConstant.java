package com.atguigu.gmall.common.constant;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 0:00
 */
public class SysRedisConstant {

    public static final String NULL_VAL = "x";
    public static final String LOCK_SKU_DETAILS = "lock:sku:details:";
    public static final long NULL_VAL_TTL = 60*30;
    public static final long SKUDETAILS_VAL_TTL = 60 * 60 * 24 * 7; //7天
    public static final String SKU_INFO_PREFIX = "sku:Info:";
    public static  final String BlOOM_SKUID = "bloom:skuId:";
    public static final String CATEGORY_KEY = "category";
    public static final int PAGESIZE = 10;
    public static final String SKU_HOTSCORE_PREFIX = "sku:hotscore:";
    public static final String LOGIN_USER_TOKEN = "login:user:token:";
    public static final String USER_HANDER = "userId";
    public static final String USER_TEMP_HANDER = "userTempId";
    public static final String CART_KEY = "cart:key:";
    public static final long MAX_CART_NUMBER = 200;
    public static final String ORDER_TEMP_TOKEN = "order:temp:tradeNo:";
    public static final Integer ORDER_CLOSE_TTL = 60 * 45;
    public static final Integer ORDER_REFUND_TTL = 60 * 60 * 24 * 30;
}
