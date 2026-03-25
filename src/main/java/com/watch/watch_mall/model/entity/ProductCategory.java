package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分类关联
 * @TableName product_category
 */
@TableName(value ="product_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory implements Serializable {

    @TableId(type = IdType.ASSIGN_ID) // 或 ASSIGN_ID
    private Long id;

    private Long productId;
    private Long categoryId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}