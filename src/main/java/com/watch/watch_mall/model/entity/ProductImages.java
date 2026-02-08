package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品图片表
 * @TableName product_images
 */
@TableName(value ="product_images")
@Data
public class ProductImages implements Serializable {
    /**
     * 图片ID (雪花算法)
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
    private String url;

    /**
     * 
     */
    private Integer isMain;

    /**
     * 
     */
    private Integer sortOrder;

    /**
     * 
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}