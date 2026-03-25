package com.watch.watch_mall.mq;

import com.watch.watch_mall.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderMqConsumer {

    @Resource
    private OrderService orderService;

    @RabbitListener(queues = OrderMqConstant.ORDER_CLOSE_RELEASE_QUEUE)
    public void handleOrderCloseRelease(OrderEventMessage message) {
        if (message == null || message.getOrderId() == null) {
            return;
        }
        boolean closed = orderService.closeExpiredOrder(message.getOrderId());
        log.info("consume order close release message, orderId={}, closed={}", message.getOrderId(), closed);
    }

    @RabbitListener(queues = OrderMqConstant.ORDER_PAY_SUCCESS_QUEUE)
    public void handleOrderPaySuccess(OrderEventMessage message) {
        if (message == null || message.getOrderId() == null) {
            return;
        }
        orderService.clearPaidOrderCartItems(message.getOrderId());
        log.info("consume order pay success message, orderId={}", message.getOrderId());
    }
}
