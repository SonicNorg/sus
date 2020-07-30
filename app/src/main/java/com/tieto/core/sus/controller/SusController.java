package com.tieto.core.sus.controller;


import com.tieto.core.sus.api.SusApi;
import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.model.ErrorCode;
import com.tieto.core.sus.model.OneOfUpdateResponseErrorResponse;
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
    public ResponseEntity<OneOfUpdateResponseErrorResponse> updateStatus(@NotNull @Valid String accountId, @NotNull @Valid String status, @Valid String msisdn) {
        DataEntity dataEntity = null;
        try {
            dataEntity = service.updateStatus(accountId, status, msisdn);
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new OneOfUpdateResponseErrorResponse()
                            .message("Ошибка доступа к БД")
                            .code(ErrorCode.NUMBER_1));
        } catch (RuntimeException ex) {
            if (SusServiceImpl.MSISDN_NOT_EQUALS.equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new OneOfUpdateResponseErrorResponse()
                                .message("Переданный msisdn не совпадает")
                                .code(ErrorCode.NUMBER_4));
            } else if (SusServiceImpl.MSISDN_NOT_FOUND.equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new OneOfUpdateResponseErrorResponse()
                                .message("Данный аккаунт не найден")
                                .code(ErrorCode.NUMBER_3));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new OneOfUpdateResponseErrorResponse()
                                .message("Произошла неопределённая ошибка, обратитесь к администратору")
                                .code(ErrorCode.NUMBER_2));
            }
        }
        if (dataEntity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OneOfUpdateResponseErrorResponse());
        }
        return ResponseEntity.ok().build();
    }
}
