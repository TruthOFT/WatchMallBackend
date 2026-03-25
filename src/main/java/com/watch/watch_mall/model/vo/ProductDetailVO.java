package com.watch.watch_mall.model.vo;

import com.watch.watch_mall.model.inner_data.FeatureItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailVO implements Serializable {
    private Long id;
    private String name;
    private String title;
    private Long brandId;
    private String description;
    private String feature;
    private List<FeatureItem> featureLst;
    private String tags;
    private BigDecimal price;
    private String mainUrl;
    private List<ProductImageVO> imageList;
    private List<CategoryVO> categoryList;
    private List<ProductSkuVO> skuList;

    private static final long serialVersionUID = 1L;
}
