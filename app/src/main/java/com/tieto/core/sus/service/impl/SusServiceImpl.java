package com.tieto.core.sus.service.impl;

import com.tieto.core.imdb.model.OneOfEnrichResponseErrorResponse;
import com.tieto.core.sus.client.ImdbFeignClient;
import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.repository.SusRepository;
import com.tieto.core.sus.service.SusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.net.SocketTimeoutException;

@Service
@Slf4j
public class SusServiceImpl implements SusService {
    public static final String MSISDN_NOT_EQUALS = "Переданный msisdn не совпадает с тем, что вернулся из IMDB";
    public static final String MSISDN_NOT_FOUND = "Ответ из IMDB без msisdn";
    private static final String SUCCESS_STATUS = "complete";
    private final SusRepository susRepository;
    private final ImdbFeignClient imdbFeignClient;

    @Autowired
    public SusServiceImpl(SusRepository susRepository, ImdbFeignClient imdbFeignClient) {
        this.susRepository = susRepository;
        this.imdbFeignClient = imdbFeignClient;
    }

    @Override
    public DataEntity updateStatus(@NotNull String accountId, @NotNull String status, @Nullable String msisdn) throws DataAccessException {
        DataEntity entity = getDataEntity(accountId, msisdn);
        if (entity == null) {
            ResponseEntity<OneOfEnrichResponseErrorResponse> responseEntity = fetchRetrying(accountId);
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.error("Request to imdb for accountId {} failed with code {}", accountId, responseEntity.getStatusCode());
                throw new RuntimeException(responseEntity.getBody() == null ? "Неизвестная ошибка InMemoryDatabase"
                        : responseEntity.getBody().getMessage());
            }
            if (responseEntity.getBody() == null) {
                log.error("Empty body with request {}", responseEntity.toString());
                throw new RuntimeException("Пустое тело ответа сервиса IMDB, но со статусом "
                        + responseEntity.getStatusCode());
            }

            if (responseEntity.getBody().getMsisdn() != null) {
                if (msisdn == null) {
                    return processSuccessStatus(accountId, responseEntity.getBody().getMsisdn(), status);
                } else if (responseEntity.getBody().getMsisdn().equals(msisdn)) {
                    return processSuccessStatus(accountId, msisdn, status);
                } else {
                    log.error(MSISDN_NOT_EQUALS);
                    throw new RuntimeException(MSISDN_NOT_EQUALS);
                }
            } else {
                log.error(MSISDN_NOT_FOUND);
                throw new RuntimeException(MSISDN_NOT_FOUND);
            }
        } else {
            int result = susRepository.updateDataEntity(accountId, status, msisdn);
            if (result != 1) {
                String message = "Обновлено " + result + " записей, хотя должна быть обновлена только одна.";
                log.error(message);
                throw new RuntimeException(message);
            }
            log.info("Updated {} enteties in DB.", result);
            return getDataEntity(accountId, msisdn); //coz we need return updated entity
        }
    }

    @Retryable(SocketTimeoutException.class)
    private ResponseEntity<OneOfEnrichResponseErrorResponse> fetchRetrying(@NotNull String accountId) {
        return imdbFeignClient.enrich(accountId);
    }

    private DataEntity getDataEntity(@NotNull String accountId, @Nullable String msisdn) throws DataAccessException {
        DataEntity entity;
        if (msisdn != null) {
            entity = susRepository.findByAccountIdAndMsisdn(accountId, msisdn);
        } else {
            entity = susRepository.findByAccountId(accountId);
        }
        return entity;
    }

    private DataEntity processSuccessStatus(@NotNull String accountId, @NotNull String msisdn, @NotNull String status) {
        int result = susRepository.createDataEntity(accountId, status, msisdn);
        if (result != 1) {
            String message = "Создано " + result + " записей, хотя должна быть создана только одна.";
            log.error(message);
            throw new RuntimeException(message);
        }
        return getDataEntity(accountId, msisdn); //coz we need return updated entity
    }
}
