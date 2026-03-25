package com.watch.watch_mall.model.dto.product;

import com.watch.watch_mall.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductAdminQueryRequest extends PageRequest implements Serializable {

    private String keyword;

    private Integer status;

    private Long categoryId;

    private static final long serialVersionUID = 1L;
}
