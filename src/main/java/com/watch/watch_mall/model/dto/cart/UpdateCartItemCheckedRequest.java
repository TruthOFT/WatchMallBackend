package com.watch.watch_mall.model.dto.cart;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateCartItemCheckedRequest implements Serializable {

    private Long id;

    private Integer checked;

    private static final long serialVersionUID = 1L;
}
