package com.atguigu.gmall.seckill.biz.impl;
import java.math.BigDecimal;

import com.atguigu.gmall.constant.MQConst;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.to.mq.SeckillTempMsg;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.seckill.SeckillConfirmVo;
import com.atguigu.gmall.rabbit.RabbitService;
import com.google.common.collect.Lists;
import com.atguigu.gmall.model.activity.CouponInfo;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import jodd.util.StringUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/20 9:33
 */
@Service
public class SeckillBizServiceImpl implements SeckillBizService {

    @Autowired
    SeckillGoodsCacheOpsService cacheOpsService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    OrderFeignClient orderFeignClient;

    @Override
    public String getgetSeckillCode(Long skuId) {

        SeckillGoods goods = cacheOpsService.getSeckillDetail(skuId);
        //查看是否存在
        if (goods == null){
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }
        //查看商品是否在秒杀期
        Date date = new Date();
        if (!date.after(goods.getStartTime())){
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }
        if (!date.before(goods.getEndTime())){
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }
        if (goods.getStockCount() <= 0L){
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }

        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();
        String c_date = DateUtil.formatDate(new Date());
        return generateCode(userId,c_date,skuId);
    }

    @Override
    public ResultCodeEnum getSeckillOrder(Long skuId, String skuIdStr) {
        //检查code是否合法
        boolean b = checkSeckillCode(skuId,skuIdStr);
        if (!b){
            return ResultCodeEnum.SECKILL_ILLEGAL;
        }
        //获取当前商品
        SeckillGoods detail = cacheOpsService.getSeckillDetail(skuId);
        if (detail == null){
            return ResultCodeEnum.SECKILL_ILLEGAL;
        }

        //查询秒杀时间
        Date date = new Date();
        if (date.before(detail.getStartTime())){
            return ResultCodeEnum.SECKILL_NO_START;
        }
        if (date.after(detail.getEndTime())){
            return ResultCodeEnum.SECKILL_END;
        }

        //看库存
        if(detail.getStockCount() <= 0){
            return ResultCodeEnum.SECKILL_FINISH;
        }

        //查看是否已经发送过请求了
        Long increment = redisTemplate.opsForValue().increment(SysRedisConstant.CACHE_SECKILL_GOODS_CODE + skuIdStr);
        if (increment > 2){
            return ResultCodeEnum.SUCCESS;
        }

        //开始秒杀

        //redis减库存
        Long decrement = redisTemplate.opsForValue()
                .decrement(SysRedisConstant.CACHE_SECKILL_GOODS_STOCK + DateUtil.formatDate(new Date()));
        if (decrement >= 0){
            //成功
            //扣本地缓存
            detail.setStockCount(detail.getStockCount() - 1);
            //在redis中保存一份临时订单
            String orderKey = SysRedisConstant.CACHE_SECKILL_GOODS_ORDER + skuIdStr;
            OrderInfo orderInfo = prepareTempSeckillOrder(skuId);
            redisTemplate.opsForValue().set(orderKey, Jsons.toStr(orderInfo));
            //发送消息修改数据库
            SeckillTempMsg tempMsg = new SeckillTempMsg(skuId, AuthUtils.getCurrentAuthUserInfo().getUserId(), skuIdStr);
            rabbitTemplate.convertAndSend(
                    MQConst.EXCHANGE_SECKILL_EVENT,
                    MQConst.RK_SECKILL_ORDERWAIT,
                    Jsons.toStr(tempMsg));
            return ResultCodeEnum.SUCCESS;
        }else {
            return ResultCodeEnum.SECKILL_FINISH;
        }
    }

    @Override
    public ResultCodeEnum checkSeckillOrderStatus(Long skuId) {

        String code = MD5.encrypt(AuthUtils.getCurrentAuthUserInfo().getUserId() + "_" + skuId + "_" + DateUtil.formatDate(new Date()));
        String json = redisTemplate.opsForValue().get(SysRedisConstant.CACHE_SECKILL_GOODS_ORDER + code);
        //未抢到单
        if (json == null){
            return ResultCodeEnum.SECKILL_RUN;
        }
        // 已经售空
        if ("x".equals(json)){
            return ResultCodeEnum.SECKILL_FINISH;
        }

        OrderInfo orderInfo = Jsons.toObj(json, OrderInfo.class);

        //秒杀成功：数据库保存成功
        if (orderInfo.getId() != null && orderInfo.getId()>0){
            return ResultCodeEnum.SECKILL_ORDER_SUCCESS;
        }
        //抢单成功： 数据库扣除成功
        if (orderInfo.getOperateTime() != null){
            return ResultCodeEnum.SECKILL_SUCCESS;
        }
        //正在排队 订单已经创建，生成临时订单
        return ResultCodeEnum.SUCCESS;
    }


    @Override
    public SeckillConfirmVo SeckillConfirmVo(Long skuId) {
        SeckillConfirmVo vo = null;

        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();
        String code = MD5.encrypt(userId + "_" + skuId + "_" + DateUtil.formatDate(new Date()));
        String json = redisTemplate.opsForValue()
                .get(SysRedisConstant.CACHE_SECKILL_GOODS_ORDER + code);
        if (json != null && !"x".equals(json)){
            OrderInfo orderInfo = Jsons.toObj(json, OrderInfo.class);
            vo = new SeckillConfirmVo();
            vo.setTempOrder(orderInfo);
            vo.setTotalNum(orderInfo.getOrderDetailList().size());
            vo.setTotalAmount(orderInfo.getTotalAmount());

            Result<List<UserAddress>> userAddress = userFeignClient.getUserAddress();
            vo.setUserAddressList(userAddress.getData());
        }
        return vo;
    }

    /**
     * 保存秒杀订单
     * @param orderInfo
     * @return
     */
    @Override
    public Long submitSeckillOrder(OrderInfo orderInfo) {
       OrderInfo dsOrder = prepareAndSaveOrderInfoForDb(orderInfo);

        return dsOrder.getId();
    }

    private OrderInfo prepareAndSaveOrderInfoForDb(OrderInfo orderInfo) {
        OrderInfo redisDate =null;

        //从redis中获取数据
        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();
        String code = MD5.encrypt(userId + "_" + orderInfo.getOrderDetailList().get(0).getSkuId() + "_" + DateUtil.formatDate(new Date()));
        String json = redisTemplate.opsForValue()
                .get(SysRedisConstant.CACHE_SECKILL_GOODS_ORDER + code);

        if (!StringUtil.isEmpty(json) && !"x".equals(json)){

            redisDate = Jsons.toObj(json, OrderInfo.class);

            redisDate.setConsignee(orderInfo.getConsignee());
            redisDate.setConsigneeTel(orderInfo.getConsigneeTel());
            redisDate.setOrderStatus(ProcessStatus.UNPAID.getOrderStatus().name());

            redisDate.setDeliveryAddress(orderInfo.getDeliveryAddress());
            redisDate.setOrderComment(orderInfo.getOrderComment());

            redisDate.setOutTradeNo(System.currentTimeMillis()+"_" + userId);

            redisDate.setCreateTime(new Date());
            Date date = new Date(System.currentTimeMillis() + 60 * 15 * 1000);
            redisDate.setExpireTime(date);
            redisDate.setProcessStatus(ProcessStatus.UNPAID.name());

            redisDate.setActivityReduceAmount(new BigDecimal("0"));
            redisDate.setCouponAmount(new BigDecimal("0"));
            redisDate.setOriginalTotalAmount(new BigDecimal("0"));
            redisDate.setRefundableTime(new Date());
            redisDate.setFeightFee(new BigDecimal("0"));
            redisDate.setOperateTime(new Date());

            //远程调用
            Result<Long> longResult = orderFeignClient.submitSeckillOrder(redisDate);
            redisDate.setId(longResult.getData());

            //更新到redis
            redisTemplate.opsForValue()
                    .set(SysRedisConstant.CACHE_SECKILL_GOODS_ORDER + code,Jsons.toStr(redisDate));
        }


        return redisDate;
    }

    private OrderInfo prepareTempSeckillOrder(Long skuId) {
        SeckillGoods detail = cacheOpsService.getSeckillDetail(skuId);
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setTotalAmount(detail.getCostPrice());
        orderInfo.setUserId(AuthUtils.getCurrentAuthUserInfo().getUserId());
        orderInfo.setTradeBody(detail.getSkuName());
        orderInfo.setImgUrl(detail.getSkuDefaultImg());

        //订单详情信息
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setSkuId(skuId);
        orderDetail.setSkuName(detail.getSkuName());
        orderDetail.setImgUrl(detail.getSkuDefaultImg());
        orderDetail.setOrderPrice(detail.getPrice());
        orderDetail.setSkuNum(1);
        orderDetail.setHasStock("1");

        orderDetail.setSplitActivityAmount(detail.getCostPrice());
        orderDetail.setSplitCouponAmount(detail.getPrice().subtract(detail.getCostPrice()));
        orderDetail.setUserId(AuthUtils.getCurrentAuthUserInfo().getUserId());

        List<OrderDetail> details = Arrays.asList(orderDetail);
        orderInfo.setOrderDetailList(details);

        return orderInfo;
    }

    private boolean checkSeckillCode(Long skuId, String skuIdStr) {
        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();
        String code = MD5.encrypt(userId + "_" + skuId + "_" + DateUtil.formatDate(new Date()));

        if (code.equals(skuIdStr) && redisTemplate.hasKey(SysRedisConstant.CACHE_SECKILL_GOODS_CODE + code)){
          return true;
        }
        return false;
    }

    private String generateCode(Long userId, String c_date, Long skuId) {
        String code = MD5.encrypt(userId + "_" + skuId + "_" + c_date);
        //redis保存一份
        redisTemplate.opsForValue().setIfAbsent(
                SysRedisConstant.CACHE_SECKILL_GOODS_CODE + code,
                "1",1, TimeUnit.DAYS);
        return code;
    }
}
