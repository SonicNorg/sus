package com.tieto.core.sus.client;

import com.tieto.core.imdb.api.ImdbApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "imdb")
public interface ImdbFeignClient extends ImdbApi {
}
