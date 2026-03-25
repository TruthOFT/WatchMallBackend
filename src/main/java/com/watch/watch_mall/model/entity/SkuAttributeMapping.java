package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 *
 * @TableName sku_attribute_mapping
 */
@TableName(value = "sku_attribute_mapping")
@Data
public class SkuAttributeMapping implements Serializable {
    @TableId(type = IdType.ASSIGN_ID) // 或 ASSIGN_ID
    private Long id;
    private Long skuId;
    private Long attributeValueId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}