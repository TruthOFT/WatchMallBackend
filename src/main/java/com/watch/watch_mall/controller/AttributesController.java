package com.watch.watch_mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.DeleteRequest;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.attribute.AttributeQueryRequest;
import com.watch.watch_mall.model.dto.attribute.AttributeSaveRequest;
import com.watch.watch_mall.model.vo.AttributeAdminDetailVO;
import com.watch.watch_mall.model.vo.AttributeAdminPageVO;
import com.watch.watch_mall.service.AttributesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attr")
public class AttributesController {

    @Resource
    private AttributesService attributesService;

    @PostMapping("/admin/add")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> addAttribute(@RequestBody AttributeSaveRequest request) {
        return ResultUtils.success(attributesService.saveAttribute(request));
    }

    @PostMapping("/admin/update")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> updateAttribute(@RequestBody AttributeSaveRequest request) {
        return ResultUtils.success(attributesService.updateAttribute(request));
    }

    @PostMapping("/admin/delete")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> deleteAttribute(@RequestBody DeleteRequest request) {
        ThrowUtils.throwIf(request == null || request.getId() == null || request.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(attributesService.deleteAttribute(request.getId()));
    }

    @PostMapping("/admin/page")
    @AuthCheck(role = "admin")
    public BaseResponse<Page<AttributeAdminPageVO>> pageAdminAttributes(@RequestBody AttributeQueryRequest request) {
        return ResultUtils.success(attributesService.pageAdminAttributes(request));
    }

    @GetMapping("/admin/detail")
    @AuthCheck(role = "admin")
    public BaseResponse<AttributeAdminDetailVO> getAdminAttributeDetail(@RequestParam("id") Long id) {
        return ResultUtils.success(attributesService.getAdminAttributeDetail(id));
    }
}
