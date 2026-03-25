package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.model.entity.ProductCategory;
import com.watch.watch_mall.service.ProductCategoryService;
import com.watch.watch_mall.mapper.ProductCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author Ginger
* @description 针对表【product_category(商品分类关联)】的数据库操作Service实现
* @createDate 2026-02-19 22:48:58
*/
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory>
    implements ProductCategoryService{

}




