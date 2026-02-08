package com.watch.watch_mall.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.watch.watch_mall.model.inner_data.FeatureItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品主表
 *
 * @TableName product
 */
@TableName(value = "product")
@Data
public class ProductVO implements Serializable {
    private Long id;
    private String name;
    private String title;
    private Long brandId;
    private String description;
    private String feature;
    private List<FeatureItem> featureLst;
    private String tags;
    private BigDecimal price;
    private Integer status;
    private Integer version;
    private String url;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}