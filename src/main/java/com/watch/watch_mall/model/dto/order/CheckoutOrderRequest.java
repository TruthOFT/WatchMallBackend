package com.watch.watch_mall.model.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckoutOrderRequest implements Serializable {

    private String remark;

    private static final long serialVersionUID = 1L;
}
