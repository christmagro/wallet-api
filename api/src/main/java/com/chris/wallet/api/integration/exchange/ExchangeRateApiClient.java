package com.chris.wallet.api.integration.exchange;

import com.chris.wallet.api.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="${feign.exchange.rate:rate}", url="${feign.exchange.rate.url:https://openexchangerates.org/}", configuration = FeignConfiguration.class)
public interface ExchangeRateApiClient extends ExchangeRateApi {
}
