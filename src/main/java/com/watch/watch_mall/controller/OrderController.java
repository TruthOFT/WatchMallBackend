package com.watch.watch_mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.order.CheckoutOrderRequest;
import com.watch.watch_mall.model.dto.order.MockPayRequest;
import com.watch.watch_mall.model.dto.order.OrderAdminQueryRequest;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.OrderAdminDetailVO;
import com.watch.watch_mall.model.vo.OrderAdminPageVO;
import com.watch.watch_mall.model.vo.OrderAdminStatsVO;
import com.watch.watch_mall.model.vo.OrderDetailVO;
import com.watch.watch_mall.model.vo.OrderVO;
import com.watch.watch_mall.service.OrderService;
import com.watch.watch_mall.service.PaymentService;
import com.watch.watch_mall.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private PaymentService paymentService;

    @Resource
    private UserService userService;

    @PostMapping("/checkout")
    public BaseResponse<OrderDetailVO> checkout(@RequestBody(required = false) CheckoutOrderRequest request,
                                                HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(orderService.checkout(loginUser.getId(), request));
    }

    @PostMapping("/pay/mock")
    public BaseResponse<Boolean> mockPay(@RequestBody MockPayRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null || request.getOrderId() == null || request.getOrderId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(paymentService.mockPay(loginUser.getId(), request));
    }

    @GetMapping("/my")
    public BaseResponse<List<OrderVO>> listMyOrders(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(orderService.listMyOrders(loginUser.getId()));
    }

    @PostMapping("/admin/page")
    @AuthCheck(role = "admin")
    public BaseResponse<Page<OrderAdminPageVO>> pageAdminOrders(@RequestBody(required = false) OrderAdminQueryRequest queryRequest) {
        return ResultUtils.success(orderService.pageAdminOrders(queryRequest));
    }

    @GetMapping("/admin/detail")
    @AuthCheck(role = "admin")
    public BaseResponse<OrderAdminDetailVO> getAdminOrderDetail(@RequestParam("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(orderService.getAdminOrderDetail(id));
    }

    @GetMapping("/admin/stats")
    @AuthCheck(role = "admin")
    public BaseResponse<OrderAdminStatsVO> getAdminOrderStats() {
        return ResultUtils.success(orderService.getAdminOrderStats());
    }

    @GetMapping("/detail")
    public BaseResponse<OrderDetailVO> getMyOrderDetail(@RequestParam("id") Long id, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(orderService.getMyOrderDetail(loginUser.getId(), id));
    }
}
