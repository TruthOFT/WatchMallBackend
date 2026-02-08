package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品主表
 * @TableName product
 */
@TableName(value ="product")
@Data
public class Product implements Serializable {
    /**
     * 商品ID (雪花算法)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private Long brandId;

    /**
     * 
     */
    private String description;

    /**
     * JSON规格
     */
    private String feature;

    /**
     * 
     */
    private String tags;

    /**
     * 因源数据缺失，默认为0.00
     */
    private BigDecimal price;

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
    private Integer isRec;

    /**
     * 
     */
    private Integer status;

    /**
     * 乐观锁
     */
    private Integer version;

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