package com.watch.watch_mall.model.dto.category;

import com.watch.watch_mall.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryQueryRequest extends PageRequest implements Serializable {

    /**
     * 分类名（模糊查询）
     */
    private String name;

    /**
     * 父级分类 id
     */
    private Long parentId;

    /**
     * 是否展示：1 展示，0 不展示
     */
    private Integer isShow;

    private static final long serialVersionUID = 1L;
}

