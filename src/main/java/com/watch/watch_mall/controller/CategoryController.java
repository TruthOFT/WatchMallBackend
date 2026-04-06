package com.watch.watch_mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.DeleteRequest;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.model.dto.category.AddCategoryRequest;
import com.watch.watch_mall.model.dto.category.CategoryQueryRequest;
import com.watch.watch_mall.model.entity.Category;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.CategoryService;
import com.watch.watch_mall.service.ProductService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private ProductService productService;

    @PostMapping("/add")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> addCategory(@RequestBody AddCategoryRequest addCategoryRequest) {
        ThrowUtils.throwIf(addCategoryRequest == null, ErrorCode.PARAMS_ERROR);
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryRequest, category);
        return ResultUtils.success(categoryService.save(category));
    }

    @PostMapping("/delete")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> deleteCategory(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(categoryService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> updateCategory(@RequestBody Category category) {
        ThrowUtils.throwIf(category == null || category.getId() == null || category.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(categoryService.updateById(category));
    }

    @GetMapping("/get")
    public BaseResponse<Category> getCategoryById(@RequestParam("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Category category = categoryService.getById(id);
        ThrowUtils.throwIf(category == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(category);
    }

    @GetMapping("/get/id")
    public BaseResponse<Long> getCategoryIdByCategoryName(@RequestParam("categoryName") String categoryName) {
        ThrowUtils.throwIf(StringUtils.isBlank(categoryName), ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<Category> queryWrapper = Wrappers.lambdaQuery(Category.class)
                .select(Category::getId)
                .eq(Category::getName, categoryName)
                .last("limit 1");
        Category category = categoryService.getOne(queryWrapper, false);
        ThrowUtils.throwIf(category == null || category.getId() == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(category.getId());
    }

    @GetMapping("/list")
    public BaseResponse<List<Category>> listCategory() {
        return ResultUtils.success(categoryService.list());
    }

    @GetMapping("/products")
    public BaseResponse<Page<ProductVO>> listCategoryProducts(@RequestParam("categoryId") Long categoryId,
                                                              @RequestParam(value = "current", defaultValue = "1") long current,
                                                              @RequestParam(value = "pageSize", defaultValue = "12") long pageSize) {
        return ResultUtils.success(productService.listProductByCategory(categoryId, current, pageSize));
    }

    @PostMapping("/list/page")
    @AuthCheck(role = "admin")
    public BaseResponse<Page<Category>> listCategoryByPage(@RequestBody CategoryQueryRequest categoryQueryRequest) {
        ThrowUtils.throwIf(categoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = categoryQueryRequest.getCurrent();
        long pageSize = categoryQueryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0 || pageSize > 100, ErrorCode.PARAMS_ERROR);

        LambdaQueryWrapper<Category> queryWrapper = Wrappers.lambdaQuery(Category.class);
        queryWrapper.like(StringUtils.isNotBlank(categoryQueryRequest.getName()),
                Category::getName, categoryQueryRequest.getName());
        queryWrapper.eq(categoryQueryRequest.getParentId() != null,
                Category::getParentId, categoryQueryRequest.getParentId());
        queryWrapper.eq(categoryQueryRequest.getIsShow() != null,
                Category::getIsShow, categoryQueryRequest.getIsShow());
        queryWrapper.orderByAsc(Category::getSortOrder).orderByDesc(Category::getCreateTime);

        Page<Category> categoryPage = categoryService.page(new Page<>(current, pageSize), queryWrapper);
        return ResultUtils.success(categoryPage);
    }
}
