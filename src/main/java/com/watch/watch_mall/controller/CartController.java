package com.watch.watch_mall.controller;

import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.DeleteRequest;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.cart.AddCartRequest;
import com.watch.watch_mall.model.dto.cart.UpdateCartItemCheckedRequest;
import com.watch.watch_mall.model.dto.cart.UpdateCartItemQuantityRequest;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.CartVO;
import com.watch.watch_mall.service.CartService;
import com.watch.watch_mall.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    private CartService cartService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addCart(@RequestBody AddCartRequest addCartRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(cartService.addCart(loginUser.getId(), addCartRequest));
    }

    @GetMapping("/my")
    public BaseResponse<CartVO> getMyCart(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(cartService.getMyCart(loginUser.getId()));
    }

    @PostMapping("/item/quantity")
    public BaseResponse<Boolean> updateCartItemQuantity(@RequestBody UpdateCartItemQuantityRequest updateRequest,
                                                        HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(cartService.updateCartItemQuantity(loginUser.getId(), updateRequest));
    }

    @PostMapping("/item/checked")
    public BaseResponse<Boolean> updateCartItemChecked(@RequestBody UpdateCartItemCheckedRequest updateRequest,
                                                       HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(cartService.updateCartItemChecked(loginUser.getId(), updateRequest));
    }

    @PostMapping("/item/delete")
    public BaseResponse<Boolean> deleteCartItem(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(cartService.deleteCartItem(loginUser.getId(), deleteRequest.getId()));
    }
}
