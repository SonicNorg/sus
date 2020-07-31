package com.tieto.core.sus.service;


import com.tieto.core.imdb.model.OneOfEnrichResponseErrorResponse;
import com.tieto.core.sus.client.ImdbFeignClient;
import com.tieto.core.sus.exception.MsisdnNotEqualsException;
import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.repository.SusRepository;
import com.tieto.core.sus.service.impl.SusServiceImpl;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SusServiceImplTest {

    private final SusRepository susRepository = Mockito.mock(SusRepository.class);
    private final ImdbFeignClient imdbFeignClient = Mockito.mock(ImdbFeignClient.class);

    private final SusService service = new SusServiceImpl(susRepository, imdbFeignClient);

    private static final String ACCOUNT_ID = "123456";
    private static final String MSISDN = "9993332211";
    private static final String STATUS = "testStatus";

    @Test
    public void updateStatusDataExistInInnerRepositoryWithoutMsisdnTest() {
        DataEntity expected = new DataEntity(MSISDN, ACCOUNT_ID, STATUS);
        Mockito.when(susRepository.findByAccountId(ACCOUNT_ID)).thenReturn(expected);
        Mockito.when(susRepository.updateDataEntity(ACCOUNT_ID, STATUS, null)).thenReturn(1);
        DataEntity actual = service.updateStatus(ACCOUNT_ID, STATUS, null);
        assertEquals(expected, actual);
        verify(susRepository, times(2)).findByAccountId(Mockito.anyString());
        verify(susRepository, times(1)).updateDataEntity(ACCOUNT_ID, STATUS, null);
    }

    @Test
    public void updateStatusDataExistInInnerRepositoryWithMsisdnTest() {
        DataEntity expected = new DataEntity(MSISDN, ACCOUNT_ID, STATUS);
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(expected);
        Mockito.when(susRepository.updateDataEntity(ACCOUNT_ID, STATUS, MSISDN)).thenReturn(1);
        DataEntity actual = service.updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(expected, actual);
        verify(susRepository, times(2)).findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        verify(susRepository, times(1)).updateDataEntity(ACCOUNT_ID, STATUS, MSISDN);
    }

    @Test
    public void createStatusDataInImdbWithMsisdnTest() {
        DataEntity expected = new DataEntity(MSISDN, ACCOUNT_ID, STATUS);
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse()
                .accountId(ACCOUNT_ID)
                .msisdn(MSISDN);
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null).thenReturn(expected);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
        Mockito.when(susRepository.createDataEntity(ACCOUNT_ID, STATUS, MSISDN)).thenReturn(1);

        DataEntity actual = service.updateStatus(ACCOUNT_ID, STATUS, MSISDN);

        assertEquals(expected, actual);
        verify(susRepository, times(2)).findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        verify(susRepository, times(1)).createDataEntity(ACCOUNT_ID, STATUS, MSISDN);
        verify(imdbFeignClient, times(1)).enrich(ACCOUNT_ID);
    }

    @Test
    public void updateStatusDataInImdbWithMsisdnTest() {
        DataEntity old = new DataEntity(MSISDN, ACCOUNT_ID, "old status");
        DataEntity expected = new DataEntity(MSISDN, ACCOUNT_ID, STATUS);
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse()
                .accountId(ACCOUNT_ID)
                .msisdn(MSISDN);
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(old).thenReturn(expected);
        Mockito.when(susRepository.updateDataEntity(ACCOUNT_ID, STATUS, MSISDN)).thenReturn(1);

        DataEntity actual = service.updateStatus(ACCOUNT_ID, STATUS, MSISDN);

        assertEquals(expected, actual);
        verify(susRepository, times(2)).findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        verify(susRepository, times(1)).updateDataEntity(ACCOUNT_ID, STATUS, MSISDN);
    }

    @Test
    public void updateStatusDataInImdbWithOutMsisdnTest() {
        DataEntity expected = new DataEntity(MSISDN, ACCOUNT_ID, STATUS);
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse()
                .accountId(ACCOUNT_ID)
                .msisdn(MSISDN);
        Mockito.when(susRepository.findByAccountId(ACCOUNT_ID)).thenReturn(null);
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(expected);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
        Mockito.when(susRepository.createDataEntity(ACCOUNT_ID, STATUS, MSISDN)).thenReturn(1);

        DataEntity actual = service.updateStatus(ACCOUNT_ID, STATUS, null);

        assertEquals(expected, actual);
        verify(susRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(susRepository, times(1)).findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        verify(susRepository, times(1)).createDataEntity(ACCOUNT_ID, STATUS, MSISDN);
        verify(imdbFeignClient, times(1)).enrich(ACCOUNT_ID);
    }

    @Test
    public void updateStatusDataInImdbEmptyBodyWithMsisdnTest() {
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, MSISDN));
    }

    @Test
    public void updateStatusDataInImdbStatusNotOKAndEmptyBodyWithMsisdnTest() {
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, MSISDN));
    }


    @Test
    public void updateStatusDataInImdbStatusNotOKAndEmptyBodyWithOutMsisdnTest() {
        Mockito.when(susRepository.findByAccountId(ACCOUNT_ID)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, null));
    }

    @Test
    public void updateStatusDataInImdbStatusNotOKAndNotEmptyBodyWithMsisdnTest() {
        String failedMessage = "failed message";
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse().message(failedMessage);
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.FORBIDDEN));

        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, MSISDN));
    }


    @Test
    public void updateStatusDataInImdbStatusNotOKAndNotEmptyBodyWithoutMessageWithMsisdnTest() {
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse();
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.FORBIDDEN));

        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, MSISDN));
    }

    @Test
    public void updateStatusDataInImdbStatusNotOKAndNotEmptyBodyWithOutMsisdnTest() {
        String failedMessage = "failed message";
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse().message(failedMessage);
        Mockito.when(susRepository.findByAccountId(ACCOUNT_ID)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.FORBIDDEN));

        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, null));
    }

    @Test
    public void updateStatusDataInImdbStatusNotOKAndNotEmptyBodyWihoutMessageWithOutMsisdnTest() {
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse();
        Mockito.when(susRepository.findByAccountId(ACCOUNT_ID)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.FORBIDDEN));

        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, null));
    }

    @Test
    public void updateStatusDataInImdbWithOutMsisdnUpdateFailedTest() {
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse()
                .accountId(ACCOUNT_ID)
                .msisdn(MSISDN);
        Mockito.when(susRepository.findByAccountId(ACCOUNT_ID)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
        Mockito.when(susRepository.createDataEntity(ACCOUNT_ID, STATUS, MSISDN)).thenReturn(-1);

        try {
            service.updateStatus(ACCOUNT_ID, STATUS, null);
        } catch (RuntimeException ex) {
            verify(susRepository, times(1)).findByAccountId(ACCOUNT_ID);
            verify(susRepository, times(1)).createDataEntity(ACCOUNT_ID, STATUS, MSISDN);
            verify(imdbFeignClient, times(1)).enrich(ACCOUNT_ID);
        }
    }

    @Test
    public void updateStatusDataInImdbWithMsisdnUpdateFailedTest() {
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse()
                .accountId(ACCOUNT_ID)
                .msisdn(MSISDN);
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
        Mockito.when(susRepository.createDataEntity(ACCOUNT_ID, STATUS, MSISDN)).thenReturn(-1);

        try {
            service.updateStatus(ACCOUNT_ID, STATUS, MSISDN);
        } catch (RuntimeException ex) {
            verify(susRepository, times(1)).findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
            verify(susRepository, times(1)).createDataEntity(ACCOUNT_ID, STATUS, MSISDN);
            verify(imdbFeignClient, times(1)).enrich(ACCOUNT_ID);
        }
    }

    @Test
    public void updateStatusDataInImdbWithMissedWithMsisdnTest() {
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenThrow(FeignException.errorStatus("/enrich", Response.builder().request(Request.create(Request.HttpMethod.GET, "/enrich", Collections.emptyMap(), "{\"accountId\": \"523456731\"}".getBytes(), StandardCharsets.UTF_8)).status(400).body("{\"accountId\": \"523456731\"}", StandardCharsets.UTF_8).build()));
        assertThrows(RuntimeException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, MSISDN));

        verify(susRepository, times(1)).findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        verify(imdbFeignClient, times(1)).enrich(ACCOUNT_ID);
    }

    @Test
    public void updateStatusDataInImdbWithAnotherWithMsisdnTest() {
        OneOfEnrichResponseErrorResponse body = new OneOfEnrichResponseErrorResponse()
                .accountId(ACCOUNT_ID)
                .msisdn("some random msisdn");
        Mockito.when(susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN)).thenReturn(null);
        Mockito.when(imdbFeignClient.enrich(ACCOUNT_ID)).thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
        assertThrows(MsisdnNotEqualsException.class, () -> service.updateStatus(ACCOUNT_ID, STATUS, MSISDN));

        verify(susRepository, times(1)).findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        verify(imdbFeignClient, times(1)).enrich(ACCOUNT_ID);
    }
}
