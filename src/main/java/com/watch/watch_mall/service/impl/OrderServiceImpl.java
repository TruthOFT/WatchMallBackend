package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.constant.OrderConstant;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.CartItemMapper;
import com.watch.watch_mall.mapper.OrderItemMapper;
import com.watch.watch_mall.mapper.OrderMapper;
import com.watch.watch_mall.mapper.UserAddressMapper;
import com.watch.watch_mall.model.dto.order.CheckoutOrderRequest;
import com.watch.watch_mall.model.dto.order.OrderAdminQueryRequest;
import com.watch.watch_mall.model.entity.CartItem;
import com.watch.watch_mall.model.entity.Order;
import com.watch.watch_mall.model.entity.OrderItem;
import com.watch.watch_mall.model.entity.ProductSkus;
import com.watch.watch_mall.model.entity.UserAddress;
import com.watch.watch_mall.model.vo.CartItemRowVO;
import com.watch.watch_mall.model.vo.OrderAdminDetailVO;
import com.watch.watch_mall.model.vo.OrderAdminPageVO;
import com.watch.watch_mall.model.vo.OrderAdminStatsVO;
import com.watch.watch_mall.model.vo.OrderDetailVO;
import com.watch.watch_mall.model.vo.OrderItemVO;
import com.watch.watch_mall.model.vo.OrderVO;
import com.watch.watch_mall.mq.OrderEventMessage;
import com.watch.watch_mall.mq.OrderMqConstant;
import com.watch.watch_mall.mq.OrderMqProducer;
import com.watch.watch_mall.service.OrderService;
import com.watch.watch_mall.service.PayLogService;
import com.watch.watch_mall.service.ProductSkusService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private CartItemMapper cartItemMapper;

    @Resource
    private ProductSkusService productSkusService;

    @Resource
    private UserAddressMapper userAddressMapper;

    @Resource
    private OrderMqProducer orderMqProducer;

    @Resource
    private PayLogService payLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO checkout(Long userId, CheckoutOrderRequest request) {
        validateUserId(userId);

        UserAddress defaultAddress = getDefaultAddress(userId);
        ThrowUtils.throwIf(defaultAddress == null, ErrorCode.OPERATION_ERROR, "请先设置默认地址");

        List<CartItem> checkedCartItems = cartItemMapper.selectList(Wrappers.lambdaQuery(CartItem.class)
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getChecked, 1)
                .eq(CartItem::getIsDelete, 0)
                .orderByAsc(CartItem::getId));
        ThrowUtils.throwIf(checkedCartItems == null || checkedCartItems.isEmpty(), ErrorCode.OPERATION_ERROR, "请先勾选要结算的商品");

        Map<Long, CartItemRowVO> cartRowMap = cartItemMapper.getMyCartItems(userId).stream()
                .collect(Collectors.toMap(CartItemRowVO::getId, row -> row, (left, right) -> left, LinkedHashMap::new));

        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<Long, ProductSkus> skuMap = new LinkedHashMap<>();
        for (CartItem cartItem : checkedCartItems) {
            CartItemRowVO cartRow = cartRowMap.get(cartItem.getId());
            ThrowUtils.throwIf(cartRow == null, ErrorCode.OPERATION_ERROR, "购物车商品已失效，请刷新后重试");
            ProductSkus sku = productSkusService.getById(cartItem.getSkuId());
            ThrowUtils.throwIf(sku == null, ErrorCode.NOT_FOUND_ERROR, "sku not found");
            int stock = defaultInt(sku.getStock());
            int lockStock = defaultInt(sku.getLockStock());
            int quantity = defaultInt(cartItem.getQuantity());
            ThrowUtils.throwIf(stock - lockStock < quantity, ErrorCode.OPERATION_ERROR, "部分商品库存不足");
            skuMap.put(sku.getId(), sku);
            totalAmount = totalAmount.add(cartItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(buildBizNo("WMO"));
        order.setOrderStatus(OrderConstant.ORDER_STATUS_PENDING_PAY);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setAddressId(defaultAddress.getId());
        order.setReceiverName(defaultAddress.getReceiverName());
        order.setReceiverPhone(defaultAddress.getReceiverPhone());
        order.setProvince(defaultAddress.getProvince());
        order.setCity(defaultAddress.getCity());
        order.setDistrict(defaultAddress.getDistrict());
        order.setDetailAddress(defaultAddress.getDetailAddress());
        order.setRemark(request == null ? null : StringUtils.trimToNull(request.getRemark()));
        boolean savedOrder = orderMapper.insert(order) > 0;
        ThrowUtils.throwIf(!savedOrder || order.getId() == null, ErrorCode.OPERATION_ERROR, "订单创建失败");

        for (CartItem cartItem : checkedCartItems) {
            CartItemRowVO cartRow = cartRowMap.get(cartItem.getId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setUserId(userId);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setSkuId(cartItem.getSkuId());
            orderItem.setSourceCartItemId(cartItem.getId());
            orderItem.setProductName(cartRow.getProductName());
            orderItem.setProductTitle(cartRow.getProductTitle());
            orderItem.setSkuName(cartRow.getSkuName());
            orderItem.setSkuImage(cartRow.getImage());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalAmount(cartItem.getPrice().multiply(BigDecimal.valueOf(defaultInt(cartItem.getQuantity()))));
            orderItemMapper.insert(orderItem);

            ProductSkus sku = skuMap.get(cartItem.getSkuId());
            sku.setLockStock(defaultInt(sku.getLockStock()) + defaultInt(cartItem.getQuantity()));
            boolean updated = productSkusService.updateById(sku);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "库存锁定失败");
        }

        OrderEventMessage message = new OrderEventMessage();
        message.setOrderId(order.getId());
        message.setOrderNo(order.getOrderNo());
        message.setUserId(userId);
        message.setEventType(OrderConstant.ORDER_EVENT_CLOSE);
        orderMqProducer.sendOrderCloseDelay(message);
        return getMyOrderDetail(userId, order.getId());
    }

    @Override
    public List<OrderVO> listMyOrders(Long userId) {
        validateUserId(userId);
        List<Order> orderList = orderMapper.selectList(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId)
                .eq(Order::getIsDelete, 0)
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getId));
        if (orderList == null || orderList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<OrderItemVO>> itemMap = listOrderItems(orderList.stream().map(Order::getId).toList());
        return orderList.stream().map(order -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setExpireTime(buildExpireTime(order.getCreateTime()));
            orderVO.setItemList(itemMap.getOrDefault(order.getId(), Collections.emptyList()));
            return orderVO;
        }).toList();
    }

    @Override
    public OrderDetailVO getMyOrderDetail(Long userId, Long orderId) {
        validateUserId(userId);
        ThrowUtils.throwIf(orderId == null || orderId <= 0, ErrorCode.PARAMS_ERROR);
        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .eq(Order::getIsDelete, 0)
                .last("limit 1"));
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "订单不存在");

        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, detailVO);
        detailVO.setExpireTime(buildExpireTime(order.getCreateTime()));
        detailVO.setItemList(listOrderItems(Collections.singletonList(orderId)).getOrDefault(orderId, Collections.emptyList()));
        return detailVO;
    }

    @Override
    public Page<OrderAdminPageVO> pageAdminOrders(OrderAdminQueryRequest queryRequest) {
        OrderAdminQueryRequest validQuery = queryRequest == null ? new OrderAdminQueryRequest() : queryRequest;
        String keyword = StringUtils.trimToNull(validQuery.getKeyword());
        Page<Order> page = new Page<>(validQuery.getCurrent(), validQuery.getPageSize());
        Page<Order> orderPage = orderMapper.selectPage(page, Wrappers.lambdaQuery(Order.class)
                .and(keyword != null, wrapper -> wrapper
                        .like(Order::getOrderNo, keyword)
                        .or()
                        .like(Order::getReceiverName, keyword)
                        .or()
                        .like(Order::getReceiverPhone, keyword))
                .eq(validQuery.getOrderStatus() != null, Order::getOrderStatus, validQuery.getOrderStatus())
                .eq(Order::getIsDelete, 0)
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getId));
        List<Order> orderList = orderPage.getRecords();
        if (orderList == null || orderList.isEmpty()) {
            return new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        }
        Map<Long, List<OrderItemVO>> itemMap = listOrderItems(orderList.stream().map(Order::getId).toList());
        List<OrderAdminPageVO> records = orderList.stream()
                .map(order -> toOrderAdminPageVO(order, itemMap.get(order.getId())))
                .toList();
        Page<OrderAdminPageVO> resultPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public OrderAdminDetailVO getAdminOrderDetail(Long orderId) {
        ThrowUtils.throwIf(orderId == null || orderId <= 0, ErrorCode.PARAMS_ERROR);
        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getId, orderId)
                .eq(Order::getIsDelete, 0)
                .last("limit 1"));
        ThrowUtils.throwIf(order == null, ErrorCode.NOT_FOUND_ERROR, "order not found");
        List<OrderItemVO> itemList = listOrderItems(Collections.singletonList(orderId))
                .getOrDefault(orderId, Collections.emptyList());
        OrderAdminDetailVO detailVO = toOrderAdminDetailVO(order, itemList);
        detailVO.setPayLogList(payLogService.listAdminPayLogsByOrderId(orderId));
        return detailVO;
    }

    @Override
    public OrderAdminStatsVO getAdminOrderStats() {
        OrderAdminStatsVO statsVO = new OrderAdminStatsVO();
        Date now = new Date();
        Date todayStart = buildTodayStart();
        statsVO.setTotalCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)));
        statsVO.setPendingCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PENDING_PAY)));
        statsVO.setPaidCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PAID)));
        statsVO.setClosedCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_CLOSED)));
        statsVO.setOverduePendingCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PENDING_PAY)
                .lt(Order::getCreateTime, new Date(now.getTime() - OrderMqConstant.ORDER_CLOSE_TTL_MILLIS))));
        statsVO.setTodayCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .ge(Order::getCreateTime, todayStart)));
        return statsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public boolean closeExpiredOrder(Long orderId) {
        ThrowUtils.throwIf(orderId == null || orderId <= 0, ErrorCode.PARAMS_ERROR);
        int updatedCount = orderMapper.update(null, Wrappers.lambdaUpdate(Order.class)
                .eq(Order::getId, orderId)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PENDING_PAY)
                .eq(Order::getIsDelete, 0)
                .set(Order::getOrderStatus, OrderConstant.ORDER_STATUS_CLOSED)
                .set(Order::getCloseTime, new Date()));
        if (updatedCount <= 0) {
            return false;
        }
        List<OrderItem> orderItems = orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                .eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getIsDelete, 0));
        for (OrderItem orderItem : orderItems) {
            ProductSkus sku = productSkusService.getById(orderItem.getSkuId());
            if (sku == null) {
                continue;
            }
            int nextLockStock = Math.max(0, defaultInt(sku.getLockStock()) - defaultInt(orderItem.getQuantity()));
            sku.setLockStock(nextLockStock);
            productSkusService.updateById(sku);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearPaidOrderCartItems(Long orderId) {
        ThrowUtils.throwIf(orderId == null || orderId <= 0, ErrorCode.PARAMS_ERROR);
        List<OrderItem> orderItems = orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                .eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getIsDelete, 0));
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getSourceCartItemId() == null) {
                continue;
            }
            cartItemMapper.deleteById(orderItem.getSourceCartItemId());
        }
    }

    private Map<Long, List<OrderItemVO>> listOrderItems(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                        .in(OrderItem::getOrderId, orderIds)
                        .eq(OrderItem::getIsDelete, 0)
                        .orderByAsc(OrderItem::getCreateTime)
                        .orderByAsc(OrderItem::getId))
                .stream()
                .map(this::toOrderItemVO)
                .collect(Collectors.groupingBy(OrderItemVO::getOrderId, LinkedHashMap::new, Collectors.toList()));
    }

    private OrderItemVO toOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orderItem, orderItemVO);
        return orderItemVO;
    }

    private OrderAdminPageVO toOrderAdminPageVO(Order order, List<OrderItemVO> itemList) {
        OrderAdminPageVO orderAdminPageVO = new OrderAdminPageVO();
        BeanUtils.copyProperties(order, orderAdminPageVO);
        orderAdminPageVO.setItemCount(sumItemQuantity(itemList));
        orderAdminPageVO.setProductSummary(buildProductSummary(itemList));
        return orderAdminPageVO;
    }

    private OrderAdminDetailVO toOrderAdminDetailVO(Order order, List<OrderItemVO> itemList) {
        OrderAdminDetailVO detailVO = new OrderAdminDetailVO();
        BeanUtils.copyProperties(order, detailVO);
        detailVO.setExpireTime(buildExpireTime(order.getCreateTime()));
        detailVO.setItemList(itemList);
        return detailVO;
    }

    private Integer sumItemQuantity(List<OrderItemVO> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return 0;
        }
        return itemList.stream()
                .map(OrderItemVO::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    private String buildProductSummary(List<OrderItemVO> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return "-";
        }
        OrderItemVO firstItem = itemList.get(0);
        String productName = StringUtils.defaultIfBlank(firstItem.getProductName(), "Unnamed product");
        int itemCount = sumItemQuantity(itemList);
        if (itemCount <= 1) {
            return productName;
        }
        return productName + " x " + itemCount;
    }

    private UserAddress getDefaultAddress(Long userId) {
        return userAddressMapper.selectOne(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
                .eq(UserAddress::getIsDelete, 0)
                .last("limit 1"));
    }

    private void validateUserId(Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
    }

    private String buildBizNo(String prefix) {
        return prefix + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + (int) (Math.random() * 9000 + 1000);
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private Date buildExpireTime(Date createTime) {
        if (createTime == null) {
            return null;
        }
        return new Date(createTime.getTime() + OrderMqConstant.ORDER_CLOSE_TTL_MILLIS);
    }

    private Date buildTodayStart() {
        Date now = new Date();
        return new Date(now.getYear(), now.getMonth(), now.getDate(), 0, 0, 0);
    }
}
