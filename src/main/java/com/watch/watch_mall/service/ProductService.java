package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.dto.product.AddProductRequest;
import com.watch.watch_mall.model.dto.product.ProductAdminQueryRequest;
import com.watch.watch_mall.model.dto.product.ProductViewTrackRequest;
import com.watch.watch_mall.model.dto.product.UpdateProductRequest;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.vo.HomeProductVO;
import com.watch.watch_mall.model.vo.ProductAdminDetailVO;
import com.watch.watch_mall.model.vo.ProductAdminPageVO;
import com.watch.watch_mall.model.vo.ProductDetailVO;
import com.watch.watch_mall.model.vo.ProductVO;

import java.util.List;

public interface ProductService extends IService<Product> {

    boolean addProduct(AddProductRequest addProductRequest);

    boolean deleteProduct(Long productId);

    boolean updateProduct(UpdateProductRequest updateProductRequest);

    HomeProductVO getHomeProductVO(Long userId);

    ProductDetailVO getProductDetail(Long productId);

    boolean trackProductView(Long userId, ProductViewTrackRequest request);

    List<ProductVO> listRelatedProducts(Long userId, Long productId, Integer size);

    Page<ProductVO> listProductByCategory(Long categoryId, long current, long pageSize);

    Page<ProductAdminPageVO> pageAdminProducts(ProductAdminQueryRequest queryRequest);

    ProductAdminDetailVO getAdminProductDetail(Long productId);
}
