package com.watch.watch_mall.service;

import com.watch.watch_mall.model.dto.order.CheckoutOrderRequest;
import com.watch.watch_mall.model.vo.OrderDetailVO;
import com.watch.watch_mall.model.vo.OrderVO;

import java.util.List;

public interface OrderService {

    OrderDetailVO checkout(Long userId, CheckoutOrderRequest request);

    List<OrderVO> listMyOrders(Long userId);

    OrderDetailVO getMyOrderDetail(Long userId, Long orderId);

    boolean closeExpiredOrder(Long orderId);

    void clearPaidOrderCartItems(Long orderId);
}
