package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.entity.ProductSimilarity;
import org.apache.ibatis.annotations.Delete;

public interface ProductSimilarityMapper extends BaseMapper<ProductSimilarity> {

    @Delete("delete from product_similarity")
    int deleteAllRows();
}
