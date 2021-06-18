package com.chris.wallet.api.integration.exchange;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface ExchangeRateApi {

    @GetMapping("/api/latest.json")
    ResponseEntity<ExchangeRateResponse> getExchangeRate(@RequestParam(value = "app_id", required = true) String appId);
}
