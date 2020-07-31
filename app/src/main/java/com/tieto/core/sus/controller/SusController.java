package com.tieto.core.sus.controller;


import com.tieto.core.sus.api.SusApi;
import com.tieto.core.sus.exception.MsisdnNotEqualsException;
import com.tieto.core.sus.exception.MsisdnNotFoundException;
import com.tieto.core.sus.model.ErrorCode;
import com.tieto.core.sus.model.OneOfUpdateResponseErrorResponse;
import com.tieto.core.sus.model.UpdateRequest;
import com.tieto.core.sus.service.SusService;
import com.tieto.core.sus.service.impl.SusServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
public class SusController implements SusApi {
    private final SusService service;

    @Autowired
    public SusController(SusService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<OneOfUpdateResponseErrorResponse> updateStatus(@Valid UpdateRequest updateRequest) {
        log.debug("update status iunput params: accountId {}, status {}, msisdn {}",
                updateRequest.getAccountId(), updateRequest.getStatus(), updateRequest.getMsisdn());
        try {
            service.updateStatus(updateRequest.getAccountId(), updateRequest.getStatus(), updateRequest.getMsisdn());
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new OneOfUpdateResponseErrorResponse()
                            .message("Ошибка доступа к БД")
                            .code(ErrorCode.NUMBER_1));
        } catch (MsisdnNotEqualsException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new OneOfUpdateResponseErrorResponse()
                            .message("Переданный msisdn не совпадает")
                            .code(ErrorCode.NUMBER_4));
        } catch (MsisdnNotFoundException ex) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new OneOfUpdateResponseErrorResponse()
                            .message("Данный аккаунт не найден")
                            .code(ErrorCode.NUMBER_3));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new OneOfUpdateResponseErrorResponse()
                            .message("Произошла неопределённая ошибка, обратитесь к администратору")
                            .code(ErrorCode.NUMBER_2));
        }
        return ResponseEntity.ok().build();
    }
}
