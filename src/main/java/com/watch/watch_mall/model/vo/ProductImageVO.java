package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductImageVO implements Serializable {
    private Long id;
    private String url;
    private Integer isMain;
    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}
