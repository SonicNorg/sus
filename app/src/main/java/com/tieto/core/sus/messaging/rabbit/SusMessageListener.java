package com.tieto.core.sus.messaging.rabbit;

import com.tieto.core.sus.exception.MsisdnNotEqualsException;
import com.tieto.core.sus.exception.MsisdnNotFoundException;
import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.model.ErrorCode;
import com.tieto.core.sus.model.MessageEntity;
import com.tieto.core.sus.model.RabbitResponse;
import com.tieto.core.sus.service.SusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
        DataEntity dataEntity;
        try {
            dataEntity = service.updateStatus(messageEntity.getAccountId(), messageEntity.getStatus(), messageEntity.getMsisdn());
            messageSender.sendMessage(new RabbitResponse(
                    new MessageEntity(dataEntity.getAccountId(), dataEntity.getStatus(), dataEntity.getMsisdn()),
                    true,
                    null,
                    null));
        } catch (DataAccessException dae) {
            messageSender.sendMessage(new RabbitResponse(
                    messageEntity,
                    false,
                    ErrorCode.NUMBER_1,
                    "Ошибка доступа к БД"));
        } catch (MsisdnNotEqualsException ex) {
            messageSender.sendMessage(new RabbitResponse(
                    messageEntity,
                    false,
                    ErrorCode.NUMBER_4,
                    "Переданный msisdn не совпадает"));
        } catch (MsisdnNotFoundException ex) {
            messageSender.sendMessage(new RabbitResponse(
                    messageEntity,
                    false,
                    ErrorCode.NUMBER_3,
                    "Данный аккаунт не найден"));
        } catch (RuntimeException ex) {
            messageSender.sendMessage(new RabbitResponse(
                    messageEntity,
                    false,
                    ErrorCode.NUMBER_2,
                    "Произошла неопределённая ошибка, обратитесь к администратору"));
        }
    }
}
