package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.entity.ProductFavorite;
import org.apache.ibatis.annotations.Param;

public interface ProductFavoriteMapper extends BaseMapper<ProductFavorite> {

    ProductFavorite selectAnyFavoriteByUserIdAndProductId(@Param("userId") Long userId,
                                                          @Param("productId") Long productId);
}
