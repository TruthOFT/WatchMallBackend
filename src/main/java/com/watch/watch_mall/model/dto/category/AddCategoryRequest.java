package com.watch.watch_mall.model.dto.category;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AddCategoryRequest implements Serializable {

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
