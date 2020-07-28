package com.tieto.core.sus.service.impl;

import com.tieto.core.sus.model.DataEntity;
import com.tieto.core.sus.repository.SusRepository;
import com.tieto.core.sus.service.SusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class SusServiceImpl implements SusService {
    private final SusRepository susRepository;
    //todo: add feign of imdb

    @Autowired
    public SusServiceImpl(SusRepository susRepository) {
        this.susRepository = susRepository;
    }

    @Override
    public DataEntity updateStatus(@NotNull String accountId, @NotNull String status, @Nullable String msisdn) {
        DataEntity entity;
        if (msisdn != null) {
            entity = susRepository.findByAccountIdAndMsisdn(accountId, msisdn);
        } else {
            entity = susRepository.findByAccountId(accountId);
        }
        if (entity == null) {
            //todo: entity = call feign of imdb.
            //todo: status = result from imdb.
        }
        if (entity == null) {
            return null;
        }
        susRepository.updateDataEntity(accountId, status);
        return entity;
    }
}
