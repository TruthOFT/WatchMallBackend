package com.watch.watch_mall.controller;

import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.DeleteRequest;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.address.SetDefaultAddressRequest;
import com.watch.watch_mall.model.dto.address.UserAddressAddRequest;
import com.watch.watch_mall.model.dto.address.UserAddressUpdateRequest;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.UserAddressVO;
import com.watch.watch_mall.service.UserAddressService;
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
@RequestMapping("/address")
public class UserAddressController {

    @Resource
    private UserAddressService userAddressService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addAddress(@RequestBody UserAddressAddRequest addRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userAddressService.addAddress(loginUser.getId(), addRequest));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateAddress(@RequestBody UserAddressUpdateRequest updateRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userAddressService.updateAddress(loginUser.getId(), updateRequest));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAddress(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userAddressService.deleteAddress(loginUser.getId(), deleteRequest.getId()));
    }

    @GetMapping("/my/list")
    public BaseResponse<List<UserAddressVO>> listMyAddress(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userAddressService.listMyAddress(loginUser.getId()));
    }

    @GetMapping("/my/detail")
    public BaseResponse<UserAddressVO> getMyAddressDetail(@RequestParam("id") Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userAddressService.getMyAddressDetail(loginUser.getId(), id));
    }

    @PostMapping("/default")
    public BaseResponse<Boolean> setDefaultAddress(@RequestBody SetDefaultAddressRequest setDefaultRequest,
                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(setDefaultRequest == null || setDefaultRequest.getId() == null || setDefaultRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userAddressService.setDefaultAddress(loginUser.getId(), setDefaultRequest.getId()));
    }
}
