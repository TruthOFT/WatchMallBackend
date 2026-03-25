package com.watch.watch_mall.controller;

import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.BusinessException;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.favorite.AddFavoriteRequest;
import com.watch.watch_mall.model.dto.favorite.RemoveFavoriteRequest;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.FavoriteProductVO;
import com.watch.watch_mall.model.vo.FavoriteStatusVO;
import com.watch.watch_mall.service.FavoriteService;
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
@RequestMapping("/favorite")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addFavorite(@RequestBody AddFavoriteRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null || request.getProductId() == null || request.getProductId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(favoriteService.addFavorite(loginUser.getId(), request.getProductId()));
    }

    @PostMapping("/remove")
    public BaseResponse<Boolean> removeFavorite(@RequestBody RemoveFavoriteRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null || request.getProductId() == null || request.getProductId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(favoriteService.removeFavorite(loginUser.getId(), request.getProductId()));
    }

    @GetMapping("/status")
    public BaseResponse<FavoriteStatusVO> getFavoriteStatus(@RequestParam("productId") Long productId,
                                                            HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(productId == null || productId <= 0, ErrorCode.PARAMS_ERROR);
        FavoriteStatusVO favoriteStatusVO = new FavoriteStatusVO();
        favoriteStatusVO.setHasFavorite(favoriteService.hasFavorite(getLoginUserIdOrNull(httpServletRequest), productId));
        return ResultUtils.success(favoriteStatusVO);
    }

    @GetMapping("/my")
    public BaseResponse<List<FavoriteProductVO>> listMyFavorites(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(favoriteService.listMyFavorites(loginUser.getId()));
    }

    private Long getLoginUserIdOrNull(HttpServletRequest request) {
        try {
            User loginUser = userService.getLoginUser(request);
            return loginUser == null ? null : loginUser.getId();
        } catch (BusinessException exception) {
            if (exception.getCode() == ErrorCode.NOT_LOGIN_ERROR.getCode()) {
                return null;
            }
            throw exception;
        }
    }
}
