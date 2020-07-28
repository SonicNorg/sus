package com.tieto.core.sus.messaging.rabbit;

import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.model.MessageEntity;
import com.tieto.core.sus.service.SusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SusMessageListener {

    private final SusService service;
    private final SusMessageSender messageSender;

    @Autowired
    public SusMessageListener(SusService service, SusMessageSender messageSender) {
        this.service = service;
        this.messageSender = messageSender;
    }

    @RabbitListener(queues = "${service.sus.rabbit.queueIn:test}")
    public void process(MessageEntity messageEntity) {
        log.debug("Received message from rabbitMQ: " + messageEntity.toString());
        DataEntity dataEntity = service.updateStatus(messageEntity.getAccountId(), messageEntity.getStatus(), messageEntity.getMsisdn());
        if (dataEntity == null) {
            messageSender.sendMessage(new MessageEntity(messageEntity.getAccountId(), "failed", null));
        } else {
            messageSender.sendMessage(new MessageEntity(dataEntity.getAccountId(), dataEntity.getStatus(), dataEntity.getMsisdn()));
        }
    }
}
