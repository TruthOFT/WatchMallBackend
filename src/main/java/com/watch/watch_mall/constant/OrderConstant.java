package com.watch.watch_mall.constant;

public interface OrderConstant {

    int ORDER_STATUS_PENDING_PAY = 0;

    int ORDER_STATUS_PAID = 1;

    int ORDER_STATUS_CLOSED = 2;

    int PAY_TYPE_MOCK = 1;

    int PAY_STATUS_SUCCESS = 1;

    String ORDER_EVENT_CLOSE = "order.close";

    String ORDER_EVENT_PAY_SUCCESS = "order.pay.success";
}
