package com.watch.watch_mall.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName product
 */
@TableName(value ="product")
@Data
public class ProductVO implements Serializable {
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
    private String categoryName;

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
    private List<String> feature;


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}