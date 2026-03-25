package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductAdminPageVO implements Serializable {

    private Long id;

    private String name;

    private String title;

    private String tags;

    private BigDecimal price;

    private Integer status;

    private Integer isHero;

    private Integer isBanner;

    private Integer isRec;

    private String mainUrl;

    private String categoryNames;

    private Long skuCount;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
