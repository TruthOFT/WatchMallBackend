package com.watch.watch_mall.mq;

public interface OrderMqConstant {

    String ORDER_EVENT_EXCHANGE = "order.event.exchange";

    String ORDER_CLOSE_DELAY_QUEUE = "order.close.delay.queue";

    String ORDER_CLOSE_RELEASE_QUEUE = "order.close.release.queue";

    String ORDER_PAY_SUCCESS_QUEUE = "order.pay.success.queue";

    String ORDER_CLOSE_DELAY_ROUTING_KEY = "order.close.delay";

    String ORDER_CLOSE_RELEASE_ROUTING_KEY = "order.close.release";

    String ORDER_PAY_SUCCESS_ROUTING_KEY = "order.pay.success";

    int ORDER_CLOSE_TTL_MILLIS = 15 * 60 * 1000;
}
