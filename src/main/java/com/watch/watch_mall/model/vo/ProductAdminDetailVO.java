package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductAdminDetailVO implements Serializable {

    private Long id;

    private String name;

    private String title;

    private Long brandId;

    private String description;

    private String feature;

    private String tags;

    private BigDecimal price;

    private Integer isHero;

    private Integer isBanner;

    private Integer isRec;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private List<Long> categoryIds;

    private List<ImageItemVO> images;

    private List<SkuItemVO> skus;

    @Data
    public static class ImageItemVO implements Serializable {
        private Long id;
        private String url;
        private Integer isMain;
        private Integer sortOrder;
        private Date createTime;
    }

    @Data
    public static class SkuItemVO implements Serializable {
        private Long id;
        private String skuCode;
        private String skuName;
        private String image;
        private BigDecimal price;
        private BigDecimal marketPrice;
        private Integer stock;
        private Integer lockStock;
        private Integer version;
        private Date createTime;
        private Date updateTime;
    }

    private static final long serialVersionUID = 1L;
}
