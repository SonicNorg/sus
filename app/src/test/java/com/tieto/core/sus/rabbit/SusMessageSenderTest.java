package com.tieto.core.sus.rabbit;

import com.tieto.core.sus.config.RabbitConfig;
import com.tieto.core.sus.messaging.rabbit.SusMessageSender;
import com.tieto.core.sus.model.MessageEntity;
import com.tieto.core.sus.model.RabbitResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SusMessageSenderTest {
    private static final String TEST_EXCHANGE = "testExchange";
    private static final String TEST_RK = "testRk";
    private final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    private final RabbitConfig rabbitConfig = Mockito.mock(RabbitConfig.class);

    private final SusMessageSender messageSender = new SusMessageSender(rabbitTemplate, rabbitConfig);

    private static final String ACCOUNT_ID = "123456";
    private static final String MSISDN = "9993332211";
    private static final String STATUS = "testStatus";

    @Test
    public void sendMessageSuccessTest() {
        MessageEntity messageEntity = new MessageEntity(ACCOUNT_ID, STATUS, MSISDN);
        Mockito.when(rabbitConfig.getExchangeOut()).thenReturn(TEST_EXCHANGE);
        Mockito.when(rabbitConfig.getRoutingKeyOut()).thenReturn(TEST_RK);
        messageSender.sendMessage(new RabbitResponse(messageEntity, true, null, null));
        verify(rabbitTemplate, times(1)).convertAndSend(TEST_EXCHANGE, TEST_RK, messageEntity);
    }
}
