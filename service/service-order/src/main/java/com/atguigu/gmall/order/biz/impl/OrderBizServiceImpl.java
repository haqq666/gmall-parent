package com.atguigu.gmall.order.biz.impl;
import java.util.Date;
import com.atguigu.gmall.model.activity.CouponInfo;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.*;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuDetailFeign;
import com.atguigu.gmall.model.cart.CartInfo;
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
    @Autowired
    OrderDetailService orderDetailService;

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

    @Override
    public List<WareChildOrderVo> orderSplit(OrderWareMapVo params) {
        //1、父订单id
        Long orderId = params.getOrderId();
        //1.1、查询父单
        OrderInfo parentOrder = orderInfoService.getById(orderId);
        //1.2、查询父单明细
        List<OrderDetail> details = orderDetailService.getOrderDetails(orderId, parentOrder.getUserId());
        parentOrder.setOrderDetailList(details);
        //==========父订单完整信息准备完成=================


        //2、库存的组合
        List<WareMapItem> items = Jsons.toObj(params.getWareSkuMap(), new TypeReference<List<WareMapItem>>() {
        });

        //3、=========== 开始拆分 ============
        List<OrderInfo> spiltOrders = items.stream()
                .map(wareMapItem -> {
                    //4、保存子订单
                    OrderInfo orderInfo = saveChildOrderInfo(wareMapItem, parentOrder);
                    return orderInfo;
                }).collect(Collectors.toList());

        //把父单状态修改为 已拆分
        orderInfoService.changeOrderStatus(parentOrder.getId(),
                parentOrder.getUserId(),
                ProcessStatus.SPLIT,
                Arrays.asList(ProcessStatus.PAID)
        );

        //4、转换为库存系统需要的数据
        return convertSpiltOrdersToWareChildOrderVo(spiltOrders);
    }

    private List<WareChildOrderVo> convertSpiltOrdersToWareChildOrderVo(List<OrderInfo> spiltOrders) {
        List<WareChildOrderVo> orderVos = spiltOrders.stream().map(orderInfo -> {
            WareChildOrderVo orderVo = new WareChildOrderVo();
            //封装:
            orderVo.setOrderId(orderInfo.getId());
            orderVo.setConsignee(orderInfo.getConsignee());
            orderVo.setConsigneeTel(orderInfo.getConsigneeTel());
            orderVo.setOrderComment(orderInfo.getOrderComment());
            orderVo.setOrderBody(orderInfo.getTradeBody());
            orderVo.setDeliveryAddress(orderInfo.getDeliveryAddress());
            orderVo.setPaymentWay(orderInfo.getPaymentWay());
            orderVo.setWareId(orderInfo.getWareId());

            //子订单明细 List<WareChildOrderDetailItemVo>  List<OrderDetail>
            List<WareChildOrderDetailItemVo> itemVos = orderInfo.getOrderDetailList()
                    .stream()
                    .map(orderDetail -> {
                        WareChildOrderDetailItemVo itemVo = new WareChildOrderDetailItemVo();
                        itemVo.setSkuId(orderDetail.getSkuId());
                        itemVo.setSkuNum(orderDetail.getSkuNum());
                        itemVo.setSkuName(orderDetail.getSkuName());
                        return itemVo;
                    }).collect(Collectors.toList());
            orderVo.setDetails(itemVos);
            return orderVo;
        }).collect(Collectors.toList());
        return orderVos;
    }


    //保存一个子订单
    private OrderInfo saveChildOrderInfo(WareMapItem wareMapItem, OrderInfo parentOrder) {
        //1、子订单中的所有商品  49,40,51
        List<Long> skuIds = wareMapItem.getSkuIds();
        //2、子订单是在哪个仓库出库的
        Long wareId = wareMapItem.getWareId();


        //3、子订单
        OrderInfo childOrderInfo = new OrderInfo();
        childOrderInfo.setConsignee(parentOrder.getConsignee());
        childOrderInfo.setConsigneeTel(parentOrder.getConsigneeTel());

        //4、获取到子订单的明细
        List<OrderDetail> childOrderDetails = parentOrder.getOrderDetailList()
                .stream()
                .filter(orderDetail -> skuIds.contains(orderDetail.getSkuId()))
                .collect(Collectors.toList());

        //流式计算
        BigDecimal decimal = childOrderDetails.stream()
                .map(orderDetail ->
                        orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum() + "")))
                .reduce((o1, o2) -> o1.add(o2))
                .get();
        //当前子订单负责所有明细的总价
        childOrderInfo.setTotalAmount(decimal);


        childOrderInfo.setOrderStatus(parentOrder.getOrderStatus());
        childOrderInfo.setUserId(parentOrder.getUserId());
        childOrderInfo.setPaymentWay(parentOrder.getPaymentWay());
        childOrderInfo.setDeliveryAddress(parentOrder.getDeliveryAddress());
        childOrderInfo.setOrderComment(parentOrder.getOrderComment());
        //对外流水号
        childOrderInfo.setOutTradeNo(parentOrder.getOutTradeNo());
        //子订单体
        childOrderInfo.setTradeBody(childOrderDetails.get(0).getSkuName());
        childOrderInfo.setCreateTime(new Date());
        childOrderInfo.setExpireTime(parentOrder.getExpireTime());
        childOrderInfo.setProcessStatus(parentOrder.getProcessStatus());


        //每个子订单未来发货以后这个都不一样
        childOrderInfo.setTrackingNo("");
        childOrderInfo.setParentOrderId(parentOrder.getId());
        childOrderInfo.setImgUrl(childOrderDetails.get(0).getImgUrl());

        //子订单的所有明细。也要保存到数据库
        childOrderInfo.setOrderDetailList(childOrderDetails);
        childOrderInfo.setWareId("" + wareId);
        childOrderInfo.setProvinceId(0L);
        childOrderInfo.setActivityReduceAmount(new BigDecimal("0"));
        childOrderInfo.setCouponAmount(new BigDecimal("0"));
        childOrderInfo.setOriginalTotalAmount(new BigDecimal("0"));

        //根据当前负责的商品决定退货时间
        childOrderInfo.setRefundableTime(parentOrder.getRefundableTime());

        childOrderInfo.setFeightFee(parentOrder.getFeightFee());
        childOrderInfo.setOperateTime(new Date());


        //保存子订单
        orderInfoService.save(childOrderInfo);

        //保存子订单的明细
        childOrderInfo.getOrderDetailList().stream().forEach(orderDetail -> orderDetail.setOrderId(childOrderInfo.getId()));

        List<OrderDetail> detailList = childOrderInfo.getOrderDetailList();
        //子单明细保存完成
        orderDetailService.saveBatch(detailList);


        return childOrderInfo;
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
