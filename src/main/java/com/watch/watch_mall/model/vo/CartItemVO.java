package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemVO implements Serializable {

    private Long id;

    private Long productId;

    private Long skuId;

    private String productName;

    private String productTitle;

    private String skuName;

    private String image;

    private BigDecimal price;

    private Integer quantity;

    private Integer checked;

    private Integer stock;

    private List<SkuAttributeValueVO> attributeValueList;

    private static final long serialVersionUID = 1L;
}
