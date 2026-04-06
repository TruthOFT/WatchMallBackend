package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductSearchIndexVO implements Serializable {

    private Long id;

    private String name;

    private String title;

    private String tags;

    private String description;

    private BigDecimal price;

    private Integer status;

    private String mainImageUrl;

    private String categoryNames;

    private Date updateTime;
}
