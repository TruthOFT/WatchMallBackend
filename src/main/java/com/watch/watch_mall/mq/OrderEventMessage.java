package com.watch.watch_mall.mq;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderEventMessage implements Serializable {

    private Long orderId;

    private String orderNo;

    private Long userId;

    private String eventType;

    private static final long serialVersionUID = 1L;
}
