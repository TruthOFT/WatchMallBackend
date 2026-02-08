package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.vo.ProductVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Ginger
 * @description 针对表【product】的数据库操作Mapper
 * @createDate 2026-01-27 15:49:13
 * @Entity generator.domain.Product
 */
public interface ProductMapper extends BaseMapper<Product> {

    @Select("select p.*, pi.url from product p " +
            "left join product_images pi " +
            "on pi.productId = p.id " +
            "where p.isRec = 1 " +
            "order by pi.sortOrder desc ;")
    List<ProductVO> getRecommendProducts();

    @Select("select p.*, pi.url from product p " +
            "left join product_images pi " +
            "on pi.productId = p.id " +
            "where p.isHero = 1 " +
            "order by pi.sortOrder desc ;")
    ProductVO getHeroProduct();

    @Select("select p.*, pi.url from product p " +
            "left join product_images pi " +
            "on pi.productId = p.id " +
            "where p.isBanner = 1 " +
            "order by pi.sortOrder desc ;")
    List<ProductVO> getBannerProducts();

}




