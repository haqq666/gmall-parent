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
    public static String BlOOM_SKUID = "bloom:skuId:";
}
