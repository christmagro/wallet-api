package com.chris.wallet.api.service;

import com.chris.wallet.api.integration.exchange.ExchangeRateResponse;

import java.math.BigDecimal;
import java.util.Optional;

public interface RateExchangeService {

    Optional<BigDecimal> getExchangeRate(String rate);

}
