package com.tieto.core.sus.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${service.sus.rabbit.queueIn:testIn}")
    private String queueIn;

    @Value("${service.sus.rabbit.queueOut:testOut}")
    private String queueOut;

    @Value("${service.sus.rabbit.exchangeIn:testExcIn}")
    private String exchangeIn;

    @Value("${service.sus.rabbit.exchangeOut:testExcOut}")
    private String exchangeOut;

    @Value("${service.sus.rabbit.routingKeyIn:testRkIn}")
    private String routingKeyIn;

    @Value("${service.sus.rabbit.routingKeyOut:testRkOut}")
    private String routingKeyOut;

    @Bean
    public Queue queueIn() {
        return new Queue(queueIn);
    }

    @Bean
    TopicExchange exchangeIn() {
        return new TopicExchange(exchangeIn);
    }

    @Bean
    Binding bindingIn(Queue queueIn, TopicExchange exchangeIn) {
        return BindingBuilder.bind(queueIn).to(exchangeIn).with(routingKeyIn);
    }

    @Bean
    public Queue queueOut() {
        return new Queue(queueOut);
    }

    @Bean
    TopicExchange exchangeOut() {
        return new TopicExchange(exchangeOut);
    }

    @Bean
    Binding bindingOut(Queue queueOut, TopicExchange exchangeOut) {
        return BindingBuilder.bind(queueOut).to(exchangeOut).with(routingKeyOut);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    public String getQueueIn() {
        return queueIn;
    }

    public String getExchangeIn() {
        return exchangeIn;
    }

    public String getRoutingKeyIn() {
        return routingKeyIn;
    }

    public String getQueueOut() {
        return queueOut;
    }

    public String getExchangeOut() {
        return exchangeOut;
    }

    public String getRoutingKeyOut() {
        return routingKeyOut;
    }
}
