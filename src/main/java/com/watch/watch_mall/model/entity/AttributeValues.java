package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName(value = "attribute_values")
@Data
public class AttributeValues implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long attributeId;

    private String value;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
