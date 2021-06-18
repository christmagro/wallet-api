package com.chris.wallet.api.service.impl;

import com.chris.wallet.api.config.WalletConfig;
import com.chris.wallet.api.exception.ExchangeRateServiceUnavailable;
import com.chris.wallet.api.integration.exchange.ExchangeRateApiClient;
import com.chris.wallet.api.integration.exchange.ExchangeRateResponse;
import com.chris.wallet.api.service.RateExchangeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateExchangeServiceImpl implements RateExchangeService {

    private final ExchangeRateApiClient exchangeRateApiClient;

    private final WalletConfig walletConfig;

    @Override
    public Optional<BigDecimal> getExchangeRate(String rate) {
        try {
            if (Objects.nonNull(walletConfig.getAppId()) && StringUtils.isNotBlank(rate)) {
                final ExchangeRateResponse exchangeRateResponse = Objects.requireNonNull(exchangeRateApiClient.getExchangeRate(walletConfig.getAppId()).getBody());
                if (Objects.nonNull(exchangeRateResponse.getRates()) && !exchangeRateResponse.getRates().isEmpty()) {
                    return Optional.ofNullable(exchangeRateResponse.getRates().get(rate));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new ExchangeRateServiceUnavailable(e.getMessage());
        }
    }

}
