package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.constant.OrderConstant;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.OrderItemMapper;
import com.watch.watch_mall.mapper.OrderMapper;
import com.watch.watch_mall.mapper.PayLogMapper;
import com.watch.watch_mall.model.dto.order.MockPayRequest;
import com.watch.watch_mall.model.entity.Order;
import com.watch.watch_mall.model.entity.OrderItem;
import com.watch.watch_mall.model.entity.PayLog;
import com.watch.watch_mall.model.entity.ProductSkus;
import com.watch.watch_mall.mq.OrderEventMessage;
import com.watch.watch_mall.mq.OrderMqConstant;
import com.watch.watch_mall.mq.OrderMqProducer;
import com.watch.watch_mall.service.OrderService;
import com.watch.watch_mall.service.PaymentService;
import com.watch.watch_mall.service.ProductSkusService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private PayLogMapper payLogMapper;

    @Resource
    private ProductSkusService productSkusService;

    @Resource
    private OrderMqProducer orderMqProducer;

    @Resource
    private OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mockPay(Long userId, MockPayRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(request == null || request.getOrderId() == null || request.getOrderId() <= 0, ErrorCode.PARAMS_ERROR);

        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getId, request.getOrderId())
                .eq(Order::getUserId, userId)
                .eq(Order::getIsDelete, 0)
                .last("limit 1"));
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        ThrowUtils.throwIf(!Objects.equals(order.getOrderStatus(), OrderConstant.ORDER_STATUS_PENDING_PAY),
                ErrorCode.OPERATION_ERROR, "当前订单不可支付");
        if (isExpired(order.getCreateTime())) {
            orderService.closeExpiredOrder(order.getId());
            ThrowUtils.throwIf(true, ErrorCode.OPERATION_ERROR, "订单已超时，请重新下单");
        }

        List<OrderItem> orderItems = orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                .eq(OrderItem::getOrderId, order.getId())
                .eq(OrderItem::getIsDelete, 0));
        ThrowUtils.throwIf(orderItems == null || orderItems.isEmpty(), ErrorCode.OPERATION_ERROR, "订单明细不存在");

        for (OrderItem orderItem : orderItems) {
            ProductSkus sku = productSkusService.getById(orderItem.getSkuId());
            ThrowUtils.throwIf(sku == null, ErrorCode.NOT_FOUND_ERROR, "sku not found");
            int quantity = defaultInt(orderItem.getQuantity());
            int stock = defaultInt(sku.getStock());
            int lockStock = defaultInt(sku.getLockStock());
            ThrowUtils.throwIf(stock < quantity || lockStock < quantity, ErrorCode.OPERATION_ERROR, "订单库存状态异常");
            sku.setStock(stock - quantity);
            sku.setLockStock(lockStock - quantity);
            boolean updated = productSkusService.updateById(sku);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "库存扣减失败");
        }

        PayLog payLog = new PayLog();
        payLog.setOrderId(order.getId());
        payLog.setUserId(userId);
        payLog.setPayNo("WMP" + System.currentTimeMillis() + (int) (Math.random() * 9000 + 1000));
        payLog.setPayType(OrderConstant.PAY_TYPE_MOCK);
        payLog.setPayStatus(OrderConstant.PAY_STATUS_SUCCESS);
        payLog.setPayAmount(order.getPayAmount());
        payLog.setPayTime(new Date());
        payLog.setRemark("支付成功");
        boolean payLogged = payLogMapper.insert(payLog) > 0;
        ThrowUtils.throwIf(!payLogged, ErrorCode.OPERATION_ERROR, "支付记录写入失败");

        int updatedCount = orderMapper.update(null, Wrappers.lambdaUpdate(Order.class)
                .eq(Order::getId, order.getId())
                .eq(Order::getUserId, userId)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PENDING_PAY)
                .eq(Order::getIsDelete, 0)
                .set(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PAID)
                .set(Order::getPayTime, new Date()));
        ThrowUtils.throwIf(updatedCount <= 0, ErrorCode.OPERATION_ERROR, "订单支付状态更新失败");

        OrderEventMessage message = new OrderEventMessage();
        message.setOrderId(order.getId());
        message.setOrderNo(order.getOrderNo());
        message.setUserId(userId);
        message.setEventType(OrderConstant.ORDER_EVENT_PAY_SUCCESS);
        orderMqProducer.sendOrderPaySuccess(message);
        return true;
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private boolean isExpired(Date createTime) {
        if (createTime == null) {
            return false;
        }
        long expireTime = createTime.getTime() + OrderMqConstant.ORDER_CLOSE_TTL_MILLIS;
        return System.currentTimeMillis() >= expireTime;
    }
}
