package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartItemRowVO implements Serializable {

    private Long id;

    private Long cartId;

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

    private static final long serialVersionUID = 1L;
}
