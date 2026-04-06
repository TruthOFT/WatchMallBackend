package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.model.dto.order.CheckoutOrderRequest;
import com.watch.watch_mall.model.dto.order.OrderAdminQueryRequest;
import com.watch.watch_mall.model.vo.OrderAdminDetailVO;
import com.watch.watch_mall.model.vo.OrderAdminPageVO;
import com.watch.watch_mall.model.vo.OrderAdminStatsVO;
import com.watch.watch_mall.model.vo.OrderDetailVO;
import com.watch.watch_mall.model.vo.OrderVO;

import java.util.List;

public interface OrderService {

    OrderDetailVO checkout(Long userId, CheckoutOrderRequest request);

    List<OrderVO> listMyOrders(Long userId);

    OrderDetailVO getMyOrderDetail(Long userId, Long orderId);

    Page<OrderAdminPageVO> pageAdminOrders(OrderAdminQueryRequest queryRequest);

    OrderAdminDetailVO getAdminOrderDetail(Long orderId);

    OrderAdminStatsVO getAdminOrderStats();

    boolean closeExpiredOrder(Long orderId);

    void clearPaidOrderCartItems(Long orderId);
}
