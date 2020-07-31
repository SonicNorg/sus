package com.tieto.core.sus.controller;


import com.tieto.core.sus.api.SusApi;
import com.tieto.core.sus.config.RateLimitConfig;
import com.tieto.core.sus.exception.MsisdnNotEqualsException;
import com.tieto.core.sus.exception.MsisdnNotFoundException;
import com.tieto.core.sus.model.ErrorCode;
import com.tieto.core.sus.model.OneOfUpdateResponseErrorResponse;
import com.tieto.core.sus.model.UpdateRequest;
import com.tieto.core.sus.service.SusService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Duration;

@RestController
@Slf4j
public class SusController implements SusApi {
    private final SusService service;
    private final RateLimitConfig config;
    private final Bucket bucket;

    @Autowired
    public SusController(SusService service, RateLimitConfig config) {
        this.service = service;
        this.config = config;
        Bandwidth limit = Bandwidth.classic(config.getRateLimitPerSecond(), Refill.greedy(config.getRateLimitPerSecond(), Duration.ofSeconds(1)));
        this.bucket = Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public ResponseEntity<OneOfUpdateResponseErrorResponse> updateStatus(@Valid UpdateRequest updateRequest) {
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        log.debug("update status iunput params: accountId {}, status {}, msisdn {}",
                updateRequest.getAccountId(), updateRequest.getStatus(), updateRequest.getMsisdn());
        try {
            service.updateStatus(updateRequest.getAccountId(), updateRequest.getStatus(), updateRequest.getMsisdn());
        } catch (DataAccessException dae) {
            log.error("Database error", dae);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new OneOfUpdateResponseErrorResponse()
                            .message("Ошибка доступа к БД")
                            .code(ErrorCode.NUMBER_1));
        } catch (MsisdnNotEqualsException ex) {
            log.debug("Msisdn not equals! Provided: {}, found: {}", ex.getProvidedMsisdn(), ex.getStoredMsisdn());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new OneOfUpdateResponseErrorResponse()
                            .message("Переданный msisdn не совпадает")
                            .code(ErrorCode.NUMBER_4));
        } catch (MsisdnNotFoundException ex) {
            log.debug("Msisdn {} not found!", updateRequest.getMsisdn());
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
