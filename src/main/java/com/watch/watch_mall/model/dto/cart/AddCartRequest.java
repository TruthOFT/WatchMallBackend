package com.watch.watch_mall.model.dto.cart;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddCartRequest implements Serializable {

    private Long skuId;

    private Integer quantity;

    private static final long serialVersionUID = 1L;
}
