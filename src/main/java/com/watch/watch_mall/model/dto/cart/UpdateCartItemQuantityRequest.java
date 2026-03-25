package com.watch.watch_mall.model.dto.cart;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateCartItemQuantityRequest implements Serializable {

    private Long id;

    private Integer quantity;

    private static final long serialVersionUID = 1L;
}
