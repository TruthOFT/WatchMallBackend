package com.watch.watch_mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.DeleteRequest;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.BusinessException;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.product.AddProductRequest;
import com.watch.watch_mall.model.dto.product.ProductAdminQueryRequest;
import com.watch.watch_mall.model.dto.product.ProductViewTrackRequest;
import com.watch.watch_mall.model.dto.product.UpdateProductRequest;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.HomeProductVO;
import com.watch.watch_mall.model.vo.ProductAdminDetailVO;
import com.watch.watch_mall.model.vo.ProductAdminPageVO;
import com.watch.watch_mall.model.vo.ProductDetailVO;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.ProductService;
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
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> addProduct(@RequestBody AddProductRequest addProductRequest) {
        return ResultUtils.success(productService.addProduct(addProductRequest));
    }

    @PostMapping("/delete")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> deleteProduct(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(productService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> updateProduct(@RequestBody UpdateProductRequest updateProductRequest) {
        return ResultUtils.success(productService.updateProduct(updateProductRequest));
    }

    @GetMapping("/list")
    public BaseResponse<List<Product>> listProduct() {
        return ResultUtils.success(productService.list());
    }

    @GetMapping("/home")
    public BaseResponse<HomeProductVO> home(HttpServletRequest request) {
        return ResultUtils.success(productService.getHomeProductVO(getLoginUserIdOrNull(request)));
    }

    @GetMapping("/detail")
    public BaseResponse<ProductDetailVO> getProductDetail(@RequestParam("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(productService.getProductDetail(id));
    }

    @PostMapping("/view/track")
    public BaseResponse<Boolean> trackProductView(@RequestBody ProductViewTrackRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null || request.getProductId() == null || request.getProductId() <= 0,
                ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(productService.trackProductView(getLoginUserIdOrNull(httpServletRequest), request));
    }

    @GetMapping("/recommend/related")
    public BaseResponse<List<ProductVO>> listRelatedProducts(@RequestParam("id") Long id,
                                                             @RequestParam(value = "size", defaultValue = "5") Integer size,
                                                             HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(productService.listRelatedProducts(getLoginUserIdOrNull(request), id, size));
    }

    @GetMapping("/list/by-category")
    public BaseResponse<Page<ProductVO>> listProductByCategory(@RequestParam("categoryId") Long categoryId,
                                                               @RequestParam(value = "current", defaultValue = "1") long current,
                                                               @RequestParam(value = "pageSize", defaultValue = "12") long pageSize) {
        return ResultUtils.success(productService.listProductByCategory(categoryId, current, pageSize));
    }

    @PostMapping("/admin/page")
    @AuthCheck(role = "admin")
    public BaseResponse<Page<ProductAdminPageVO>> pageAdminProducts(@RequestBody ProductAdminQueryRequest queryRequest) {
        return ResultUtils.success(productService.pageAdminProducts(queryRequest));
    }

    @GetMapping("/admin/detail")
    @AuthCheck(role = "admin")
    public BaseResponse<ProductAdminDetailVO> getAdminProductDetail(@RequestParam("id") Long id) {
        return ResultUtils.success(productService.getAdminProductDetail(id));
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
