package com.watch.watch_mall.mq;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderMqProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendOrderCloseDelay(OrderEventMessage message) {
        rabbitTemplate.convertAndSend(
                OrderMqConstant.ORDER_EVENT_EXCHANGE,
                OrderMqConstant.ORDER_CLOSE_DELAY_ROUTING_KEY,
                message
        );
    }

    public void sendOrderPaySuccess(OrderEventMessage message) {
        rabbitTemplate.convertAndSend(
                OrderMqConstant.ORDER_EVENT_EXCHANGE,
                OrderMqConstant.ORDER_PAY_SUCCESS_ROUTING_KEY,
                message
        );
    }
}
