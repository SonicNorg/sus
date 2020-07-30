package com.tieto.core.sus.controller;

import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.model.ErrorCode;
import com.tieto.core.sus.model.OneOfUpdateResponseErrorResponse;
import com.tieto.core.sus.service.SusService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.tieto.core.sus.service.impl.SusServiceImpl.MSISDN_NOT_EQUALS;
import static com.tieto.core.sus.service.impl.SusServiceImpl.MSISDN_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SusControllerTest {
    private final SusService service = Mockito.mock(SusService.class);

    private final SusController controller = new SusController(service);

    private static final String ACCOUNT_ID = "123456";
    private static final String MSISDN = "9993332211";
    private static final String STATUS = "testStatus";

    @Test
    public void updateStatusSuccessTest() {
        Mockito.when(service.updateStatus(ACCOUNT_ID, STATUS, MSISDN)).thenReturn(new DataEntity(MSISDN, ACCOUNT_ID, STATUS));
        ResponseEntity<OneOfUpdateResponseErrorResponse> resp = controller.updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        verify(service, times(1)).updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(HttpStatus.OK ,resp.getStatusCode());
    }

    @Test
    public void updateStatusDBErrorTest() {
        Mockito.when(service.updateStatus(ACCOUNT_ID, STATUS, MSISDN))
                .thenThrow(new PermissionDeniedDataAccessException("test msg", null));
        ResponseEntity<OneOfUpdateResponseErrorResponse> resp = controller.updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        verify(service, times(1)).updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Ошибка доступа к БД", resp.getBody().getMessage());
        assertEquals(ErrorCode.NUMBER_1, resp.getBody().getCode());
    }

    @Test
    public void updateStatusMsisdnNotEqualErrorTest() {
        Mockito.when(service.updateStatus(ACCOUNT_ID, STATUS, MSISDN))
                .thenThrow(new RuntimeException(MSISDN_NOT_EQUALS, null));
        ResponseEntity<OneOfUpdateResponseErrorResponse> resp = controller.updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        verify(service, times(1)).updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Переданный msisdn не совпадает", resp.getBody().getMessage());
        assertEquals(ErrorCode.NUMBER_4, resp.getBody().getCode());
    }

    @Test
    public void updateStatusMsisdnNotFoundErrorTest() {
        Mockito.when(service.updateStatus(ACCOUNT_ID, STATUS, MSISDN))
                .thenThrow(new RuntimeException(MSISDN_NOT_FOUND, null));
        ResponseEntity<OneOfUpdateResponseErrorResponse> resp = controller.updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        verify(service, times(1)).updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Данный аккаунт не найден", resp.getBody().getMessage());
        assertEquals(ErrorCode.NUMBER_3, resp.getBody().getCode());
    }

    @Test
    public void updateStatusUnknownExceptionErrorTest() {
        Mockito.when(service.updateStatus(ACCOUNT_ID, STATUS, MSISDN))
                .thenThrow(new RuntimeException("some random exc", null));
        ResponseEntity<OneOfUpdateResponseErrorResponse> resp = controller.updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        verify(service, times(1)).updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Произошла неопределённая ошибка, обратитесь к администратору", resp.getBody().getMessage());
        assertEquals(ErrorCode.NUMBER_2, resp.getBody().getCode());
    }

}
