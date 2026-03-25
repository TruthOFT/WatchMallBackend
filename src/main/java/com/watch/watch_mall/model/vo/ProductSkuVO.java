package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSkuVO implements Serializable {
    private Long id;
    private String skuCode;
    private String skuName;
    private String image;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private Integer stock;
    private List<SkuAttributeValueVO> attributeValueList;

    private static final long serialVersionUID = 1L;
}
