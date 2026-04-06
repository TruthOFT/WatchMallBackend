package com.watch.watch_mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.pay.PayLogAdminQueryRequest;
import com.watch.watch_mall.model.vo.PayLogAdminDetailVO;
import com.watch.watch_mall.model.vo.PayLogAdminPageVO;
import com.watch.watch_mall.service.PayLogService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
public class PayLogController {

    @Resource
    private PayLogService payLogService;

    @PostMapping("/admin/page")
    @AuthCheck(role = "admin")
    public BaseResponse<Page<PayLogAdminPageVO>> pageAdminPayLogs(@RequestBody(required = false) PayLogAdminQueryRequest queryRequest) {
        return ResultUtils.success(payLogService.pageAdminPayLogs(queryRequest));
    }

    @GetMapping("/admin/detail")
    @AuthCheck(role = "admin")
    public BaseResponse<PayLogAdminDetailVO> getAdminPayLogDetail(@RequestParam("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(payLogService.getAdminPayLogDetail(id));
    }
}
