package com.watch.watch_mall.model.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductSearchDocument {

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
