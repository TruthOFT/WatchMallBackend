package com.watch.watch_mall.model.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest implements Serializable {
    // Product
    String name;
    String title;
    String description;
    String tags;
    String feature;
    BigDecimal price;
    Integer isHero;
    Integer isBanner;
    Integer isRec;
    Integer status;

    private List<Long> categoryIds;          // product_category
    private List<ImageItem> images;          // product_images
    private List<SkuItem> skus;              // product_skus

    @Data
    public static class ImageItem {
        private String url;
        private Integer isMain;
        private Integer sortOrder;
    }

    @Data
    public static class SkuItem {
        private String skuCode;
        private String skuName;
        private String image;
        private BigDecimal price;
        private BigDecimal marketPrice;
        private Integer stock;
        private Integer lockStock;
    }
}
