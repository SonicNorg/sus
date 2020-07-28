package com.tieto.core.sus.service;

import com.tieto.core.sus.model.DataEntity;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

public interface SusService {
    DataEntity updateStatus(@NotNull String accountId, @NotNull String status, @Nullable String msisdn);
}
