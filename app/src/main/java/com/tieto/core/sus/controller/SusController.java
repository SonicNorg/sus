package com.tieto.core.sus.controller;


import com.tieto.core.sus.api.SusApi;
import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.service.SusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Void> updateStatus(@NotNull @Valid String accountId, @NotNull @Valid String status, @Valid String msisdn) {
        DataEntity dataEntity = service.updateStatus(accountId, status, msisdn);
        if (dataEntity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
}
