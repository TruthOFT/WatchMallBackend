package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItemVO implements Serializable {

    private Long id;

    private Long orderId;

    private Long productId;

    private Long skuId;

    private String productName;

    private String productTitle;

    private String skuName;

    private String skuImage;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalAmount;

    private static final long serialVersionUID = 1L;
}
