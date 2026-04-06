package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.model.dto.product.ProductAdminQueryRequest;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.ProductImages;
import com.watch.watch_mall.model.entity.ProductSkus;
import com.watch.watch_mall.model.vo.CategoryVO;
import com.watch.watch_mall.model.vo.ProductAdminPageVO;
import com.watch.watch_mall.model.vo.ProductSearchIndexVO;
import com.watch.watch_mall.model.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    List<ProductVO> getRecommendProducts();

    ProductVO getHeroProduct();

    List<ProductVO> getBannerProducts();

    ProductVO getProductBaseById(@Param("productId") Long productId);

    List<ProductImages> getProductImagesByProductId(@Param("productId") Long productId);

    List<CategoryVO> getCategoryListByProductId(@Param("productId") Long productId);

    List<ProductSkus> getSkuListByProductId(@Param("productId") Long productId);

    Page<ProductVO> listProductByCategory(Page<ProductVO> page, @Param("categoryId") Long categoryId);

    Page<ProductAdminPageVO> pageAdminProducts(Page<ProductAdminPageVO> page, @Param("query") ProductAdminQueryRequest query);

    List<Long> getCategoryIdListByProductId(@Param("productId") Long productId);

    ProductSearchIndexVO getSearchProductById(@Param("productId") Long productId);

    List<ProductSearchIndexVO> listSearchProducts();
}
