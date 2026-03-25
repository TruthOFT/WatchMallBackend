package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuAttributeValueVO implements Serializable {
    private Long attributeId;
    private String attributeName;
    private Long attributeValueId;
    private String attributeValue;

    private static final long serialVersionUID = 1L;
}
