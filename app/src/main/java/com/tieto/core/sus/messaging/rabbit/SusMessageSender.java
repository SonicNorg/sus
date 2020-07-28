package com.tieto.core.sus.messaging.rabbit;

import com.tieto.core.sus.config.RabbitConfig;
import com.tieto.core.sus.model.MessageEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SusMessageSender {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig rabbitConfig;

    @Autowired
    public SusMessageSender(RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

    public void sendMessage(MessageEntity messageEntity) {
        this.rabbitTemplate.convertAndSend(rabbitConfig.getExchangeOut(), rabbitConfig.getRoutingKeyOut(), messageEntity);
    }
}
