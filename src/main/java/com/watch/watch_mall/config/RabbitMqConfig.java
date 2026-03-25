package com.watch.watch_mall.config;

import com.watch.watch_mall.mq.OrderMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public DirectExchange orderEventExchange() {
        return new DirectExchange(OrderMqConstant.ORDER_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderCloseDelayQueue() {
        return QueueBuilder.durable(OrderMqConstant.ORDER_CLOSE_DELAY_QUEUE)
                .ttl(OrderMqConstant.ORDER_CLOSE_TTL_MILLIS)
                .deadLetterExchange(OrderMqConstant.ORDER_EVENT_EXCHANGE)
                .deadLetterRoutingKey(OrderMqConstant.ORDER_CLOSE_RELEASE_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue orderCloseReleaseQueue() {
        return QueueBuilder.durable(OrderMqConstant.ORDER_CLOSE_RELEASE_QUEUE).build();
    }

    @Bean
    public Queue orderPaySuccessQueue() {
        return QueueBuilder.durable(OrderMqConstant.ORDER_PAY_SUCCESS_QUEUE).build();
    }

    @Bean
    public Binding orderCloseDelayBinding() {
        return BindingBuilder.bind(orderCloseDelayQueue())
                .to(orderEventExchange())
                .with(OrderMqConstant.ORDER_CLOSE_DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding orderCloseReleaseBinding() {
        return BindingBuilder.bind(orderCloseReleaseQueue())
                .to(orderEventExchange())
                .with(OrderMqConstant.ORDER_CLOSE_RELEASE_ROUTING_KEY);
    }

    @Bean
    public Binding orderPaySuccessBinding() {
        return BindingBuilder.bind(orderPaySuccessQueue())
                .to(orderEventExchange())
                .with(OrderMqConstant.ORDER_PAY_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        return rabbitTemplate;
    }
}
