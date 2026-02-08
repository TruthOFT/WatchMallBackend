package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品分类表
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category implements Serializable {
    /**
     * 分类ID (雪花算法)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 
     */
    private String icon;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private Integer sortOrder;

    /**
     * 
     */
    private Integer isShow;

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