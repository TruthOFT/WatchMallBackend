package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName product
 */
@TableName(value ="product")
@Data
public class Product implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String productName;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private String imageUrl;

    /**
     * 
     */
    private String tag;

    /**
     * 
     */
    private BigDecimal price;

    /**
     * 
     */
    private Integer stock;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private Integer isHero;

    /**
     * 
     */
    private Integer isBanner;

    /**
     * 
     */
    private Integer isChoice;

    /**
     * 
     */
    private Integer isRec;

    /**
     * 
     */
    private String feature;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}