package com.atguigu.gmall.order.service.impl;

import java.math.BigDecimal;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.constant.MQConst;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 乔豆麻担
 * @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
 * @createDate 2022-09-12 22:10:51
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {

    @Resource
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public Long saveOrder(OrderSubmitVo submitVo, String tradeNo) {

        //保存订单信息
        OrderInfo orderInfo = prepareOrderInfo(submitVo, tradeNo);
        orderInfoMapper.insert(orderInfo);

        //保存明细信息
        List<OrderDetail> details = prepareOrderDetail(submitVo, orderInfo);
        orderDetailService.saveBatch(details);

        //给mq发消息，45分钟后改变订单状态
        OrderMsg orderMsg = new OrderMsg(orderInfo.getUserId(), orderInfo.getId());
        rabbitTemplate.convertAndSend(MQConst.EXCHANGE_ORDER_EVENT,
                MQConst.RK_ORDER_CREATED,
                Jsons.toStr(orderMsg));

        return orderInfo.getId();
    }

    @Override
    public void changeOrderStatus(Long orderId,
                                  Long userId,
                                  ProcessStatus whileChange,
                                  List<ProcessStatus> expected) {

        String orderStatus = whileChange.getOrderStatus().name();
        String processStatus = whileChange.name();

        List<String> expects = expected.stream().map(Enum::name).collect(Collectors.toList());

        //幂等修改订单
        orderInfoMapper.changeOrderStatus(orderId, userId, orderStatus,  processStatus, expects);
    }


    @Override
    public OrderInfo getOrderInfoByOutTradeNumberAndUserId(String outTradeNo, long userId) {

        return getOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getOutTradeNo, outTradeNo)
                .eq(OrderInfo::getUserId, userId));

    }

    @Override
    public OrderInfo getOrderInfoByOrderIdAndUserId(Long orderId, Long userId) {

        return getOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .eq(OrderInfo::getUserId, userId));
    }

    private List<OrderDetail> prepareOrderDetail(OrderSubmitVo submitVo, OrderInfo orderInfo) {
        List<OrderDetail> detailList = submitVo.getOrderDetailList().stream()
                .map(vo -> {
                    OrderDetail orderDetail = new OrderDetail();

                    orderDetail.setOrderId(orderInfo.getId());

                    orderDetail.setSkuId(vo.getSkuId());

                    orderDetail.setSkuName(vo.getSkuName());

                    orderDetail.setImgUrl(vo.getImgUrl());

                    orderDetail.setOrderPrice(vo.getOrderPrice());

                    orderDetail.setSkuNum(vo.getSkuNum());

                    orderDetail.setHasStock(vo.getHasStock());

                    orderDetail.setCreateTime(new Date());

                    orderDetail.setSplitTotalAmount(vo.getOrderPrice()
                            .multiply(new BigDecimal(vo.getSkuNum() + "")));

                    orderDetail.setSplitActivityAmount(new BigDecimal("0"));

                    orderDetail.setSplitCouponAmount(new BigDecimal("0"));


                    orderDetail.setUserId(orderInfo.getUserId());

                    return orderDetail;
                }).collect(Collectors.toList());

        return detailList;
    }

    private OrderInfo prepareOrderInfo(OrderSubmitVo submitVo, String tradeNo) {

        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setConsignee(submitVo.getConsignee());
        orderInfo.setConsigneeTel(submitVo.getConsigneeTel());

        Long userId = AuthUtils.getCurrentAuthUserInfo().getUserId();
        orderInfo.setUserId(userId);

        orderInfo.setPaymentWay(submitVo.getPaymentWay());

        orderInfo.setDeliveryAddress(submitVo.getDeliveryAddress());

        orderInfo.setOrderComment(submitVo.getOrderComment());

        orderInfo.setOutTradeNo(tradeNo);

        orderInfo.setTradeBody(submitVo.getOrderDetailList().get(0).getSkuName());

        orderInfo.setCreateTime(new Date());

        orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1000L * SysRedisConstant.ORDER_CLOSE_TTL));

        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());

        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());

        orderInfo.setTrackingNo("");

        orderInfo.setParentOrderId(0L);

        orderInfo.setImgUrl(submitVo.getOrderDetailList().get(0).getImgUrl());

        orderInfo.setActivityReduceAmount(new BigDecimal("0"));

        orderInfo.setCouponAmount(new BigDecimal("0"));

        BigDecimal totalAmount = submitVo.getOrderDetailList().stream()
                .map(o -> o.getOrderPrice()
                        .multiply(new BigDecimal(o.getSkuNum() + "")))
                .reduce(BigDecimal::add).get();
        orderInfo.setTotalAmount(totalAmount);

        orderInfo.setOriginalTotalAmount(totalAmount);

        orderInfo.setRefundableTime(new Date(System.currentTimeMillis() + SysRedisConstant.ORDER_REFUND_TTL * 1000));

        orderInfo.setFeightFee(new BigDecimal("0"));

        orderInfo.setOperateTime(new Date());

        return orderInfo;
    }
}




