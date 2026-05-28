package com.watch.watch_mall.service.impl;

import com.watch.watch_mall.model.vo.DashboardSummaryVO;
import com.watch.watch_mall.model.vo.OrderAdminPageVO;
import com.watch.watch_mall.service.DashboardService;
import com.watch.watch_mall.service.OrderService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private OrderService orderService;

    @Override
    public DashboardSummaryVO getAdminSummary() {
        DashboardSummaryVO summaryVO = new DashboardSummaryVO();
        List<OrderAdminPageVO> recentOrderList = orderService.pageAdminOrders(null).getRecords();
        summaryVO.setRecentOrderList(recentOrderList == null ? List.of() : recentOrderList.stream().limit(5).toList());
        return summaryVO;
    }
}
