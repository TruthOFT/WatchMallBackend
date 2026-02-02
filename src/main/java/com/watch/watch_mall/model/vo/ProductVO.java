package com.watch.watch_mall.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.watch.watch_mall.model.inner_data.FeatureItem;
import lombok.Data;

import java.io.Serializable;
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
    private List<FeatureItem> feature;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}