package com.watch.watch_mall.controller;

import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.model.vo.DashboardSummaryVO;
import com.watch.watch_mall.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/admin/summary")
    @AuthCheck(role = "admin")
    public BaseResponse<DashboardSummaryVO> getAdminSummary() {
        return ResultUtils.success(dashboardService.getAdminSummary());
    }
}
