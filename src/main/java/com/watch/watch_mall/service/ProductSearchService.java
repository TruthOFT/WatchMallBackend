package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.model.vo.ProductVO;

public interface ProductSearchService {

    void syncProductById(Long productId);

    void deleteProductById(Long productId);

    long rebuildProductIndex();

    Page<ProductVO> searchProducts(String keyword, long current, long pageSize);
}
