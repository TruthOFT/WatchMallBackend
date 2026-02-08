package com.watch.watch_mall.controller;

import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.DeleteRequest;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.vo.HomeProductVO;
import com.watch.watch_mall.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    ProductService productService;

    /**
     * 增
     *
     * @param product
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addProduct(@RequestPart("product") Product product, @RequestPart("file") MultipartFile file) {
        return ResultUtils.success(productService.addProduct(product, file));
    }

    /**
     * 删
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteProduct(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(productService.removeById(deleteRequest.getId()));
    }

    /**
     * 改
     *
     * @param product
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateProduct(@RequestBody Product product) {
        return ResultUtils.success(productService.updateById(product));
    }

    @GetMapping("/list")
    public BaseResponse<List<Product>> listProduct() {
        return ResultUtils.success(productService.list());
    }

    /**
     * 上传商品图片
     *
     * @param file
     * @return
     */
    @PostMapping("/upload/image")
    public BaseResponse<String> uploadImage(@RequestParam(value = "file", required = false) MultipartFile file) {
        ThrowUtils.throwIf(file == null, ErrorCode.PARAMS_ERROR, "文件为空");
        return ResultUtils.success(productService.uploadFile(file));
    }

    @GetMapping("/home")
    public BaseResponse<HomeProductVO> home() {
        return ResultUtils.success(productService.getHomeProductVO());
    }
}
