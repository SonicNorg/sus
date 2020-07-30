package com.tieto.core.sus.rabbit;

import com.tieto.core.sus.messaging.rabbit.SusMessageListener;
import com.tieto.core.sus.messaging.rabbit.SusMessageSender;
import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.model.MessageEntity;
import com.tieto.core.sus.service.SusService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SusMessageListenerTest {

    private final SusService service = Mockito.mock(SusService.class);
    private final SusMessageSender messageSender = Mockito.mock(SusMessageSender.class);

    private final SusMessageListener listener = new SusMessageListener(service, messageSender);

    private static final String ACCOUNT_ID = "123456";
    private static final String MSISDN = "9993332211";
    private static final String STATUS = "testStatus";

    @Test
    public void processSuccessTest() {
        MessageEntity messageEntity = new MessageEntity(ACCOUNT_ID, STATUS, MSISDN);

        Mockito.when(service.updateStatus(messageEntity.getAccountId(), messageEntity.getStatus(), messageEntity.getMsisdn()))
                .thenReturn(new DataEntity(MSISDN, ACCOUNT_ID, STATUS));
        listener.process(messageEntity);
        verify(service, times(1)).updateStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(messageSender, times(1)).sendMessage(Mockito.any());
    }

    @Test
    public void processExceptionTest() {
        MessageEntity messageEntity = new MessageEntity(ACCOUNT_ID, STATUS, MSISDN);

        Mockito.when(service.updateStatus(messageEntity.getAccountId(), messageEntity.getStatus(), messageEntity.getMsisdn()))
                .thenThrow(new RuntimeException("test exc"));
        listener.process(messageEntity);
        verify(service, times(1)).updateStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(messageSender, times(1)).sendMessage(
                new MessageEntity(messageEntity.getAccountId(), "failed", messageEntity.getMsisdn()));
    }

    @Test
    public void processNotFoundEntityTest() {
        MessageEntity messageEntity = new MessageEntity(ACCOUNT_ID, STATUS, MSISDN);

        Mockito.when(service.updateStatus(messageEntity.getAccountId(), messageEntity.getStatus(), messageEntity.getMsisdn()))
                .thenReturn(null);
        listener.process(messageEntity);
        verify(service, times(1)).updateStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(messageSender, times(1)).sendMessage(
                new MessageEntity(messageEntity.getAccountId(), "failed", messageEntity.getMsisdn()));
    }
}
