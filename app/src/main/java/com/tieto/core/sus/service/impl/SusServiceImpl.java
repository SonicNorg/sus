package com.tieto.core.sus.service.impl;

import com.tieto.core.imdb.model.OneOfEnrichResponseErrorResponse;
import com.tieto.core.sus.client.ImdbFeignClient;
import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.repository.SusRepository;
import com.tieto.core.sus.service.SusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class SusServiceImpl implements SusService {
    private final SusRepository susRepository;
    private final ImdbFeignClient imdbFeignClient;

    @Autowired
    public SusServiceImpl(SusRepository susRepository, ImdbFeignClient imdbFeignClient) {
        this.susRepository = susRepository;
        this.imdbFeignClient = imdbFeignClient;
    }

    @Override
    public DataEntity updateStatus(@NotNull String accountId, @NotNull String status, @Nullable String msisdn) {
        DataEntity entity = getDataEntity(accountId, msisdn);
        if (entity == null) {
            ResponseEntity<OneOfEnrichResponseErrorResponse> responseEntity = imdbFeignClient.enrich(accountId);
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException(responseEntity.getBody() == null ? "Неизвестная ошибка InMemoryDatabase"
                        : responseEntity.getBody().getMessage());
            }
            if (responseEntity.getBody() == null) {
                throw new RuntimeException("Пустое тело ответа сервиса IMDB, но со статусом "
                        + responseEntity.getStatusCode());
            }

            if (responseEntity.getBody().getMsisdn() != null) {
                if (responseEntity.getBody().getMsisdn().equals(msisdn)) {
                    status = "complete";
                    msisdn = responseEntity.getBody().getMsisdn();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        int result = susRepository.updateDataEntity(accountId, status, msisdn);
        if (result != 1) {
            throw new RuntimeException("Обновлено " + result + " записей, хотя должна быть обновлена только одна.");
        }
        return getDataEntity(accountId, msisdn); //coz we need return updated entity
    }

    private DataEntity getDataEntity(@NotNull String accountId, @Nullable String msisdn) {
        DataEntity entity;
        if (msisdn != null) {
            entity = susRepository.findByAccountIdAndMsisdn(accountId, msisdn);
        } else {
            entity = susRepository.findByAccountId(accountId);
        }
        return entity;
    }
}
