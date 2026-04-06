package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.watch.watch_mall.constant.OrderConstant;
import com.watch.watch_mall.mapper.OrderMapper;
import com.watch.watch_mall.mapper.ProductMapper;
import com.watch.watch_mall.mapper.UserMapper;
import com.watch.watch_mall.model.entity.Order;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.DashboardSummaryVO;
import com.watch.watch_mall.model.vo.OrderAdminPageVO;
import com.watch.watch_mall.service.DashboardService;
import com.watch.watch_mall.service.OrderService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private OrderService orderService;

    @Override
    public DashboardSummaryVO getAdminSummary() {
        DashboardSummaryVO summaryVO = new DashboardSummaryVO();
        Date todayStart = buildTodayStart();
        summaryVO.setTodayOrderCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .ge(Order::getCreateTime, todayStart)));
        summaryVO.setPaidOrderCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PAID)));
        summaryVO.setPendingOrderCount(orderMapper.selectCount(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getIsDelete, 0)
                .eq(Order::getOrderStatus, OrderConstant.ORDER_STATUS_PENDING_PAY)));
        summaryVO.setTotalUserCount(userMapper.selectCount(Wrappers.lambdaQuery(User.class)
                .eq(User::getIsDelete, 0)));
        summaryVO.setTotalProductCount(productMapper.selectCount(Wrappers.lambdaQuery(Product.class)
                .eq(Product::getIsDelete, 0)));
        List<OrderAdminPageVO> recentOrderList = orderService.pageAdminOrders(null).getRecords();
        summaryVO.setRecentOrderList(recentOrderList == null ? List.of() : recentOrderList.stream().limit(5).toList());
        return summaryVO;
    }

    private Date buildTodayStart() {
        Date now = new Date();
        return new Date(now.getYear(), now.getMonth(), now.getDate(), 0, 0, 0);
    }
}
