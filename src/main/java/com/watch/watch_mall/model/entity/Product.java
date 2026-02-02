package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
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
    @TableId(type = IdType.ASSIGN_ID)
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
    private String title;

    /**
     * 
     */
    private String feature;

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
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}