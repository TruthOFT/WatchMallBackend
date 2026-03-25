package com.watch.watch_mall.model.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class MockPayRequest implements Serializable {

    private Long orderId;

    private static final long serialVersionUID = 1L;
}
