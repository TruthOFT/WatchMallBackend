package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 商品SKU表
 * @TableName product_skus
 */
@TableName(value ="product_skus")
@Data
public class ProductSkus implements Serializable {
    /**
     * SKU ID (雪花算法)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 
     */
    private Long productId;

    /**
     * 
     */
    private String skuCode;

    /**
     * 
     */
    private String skuName;

    /**
     * 
     */
    private String image;

    /**
     * 
     */
    private BigDecimal price;

    /**
     * 
     */
    private BigDecimal marketPrice;

    /**
     * 
     */
    private Integer stock;

    /**
     * 
     */
    private Integer lockStock;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}