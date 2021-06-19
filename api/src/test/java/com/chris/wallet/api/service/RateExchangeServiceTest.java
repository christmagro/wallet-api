package com.chris.wallet.api.service;

import com.chris.wallet.api.config.WalletConfig;
import com.chris.wallet.api.exception.ExchangeRateServiceUnavailableExceptions;
import com.chris.wallet.api.integration.exchange.ExchangeRateApiClient;
import com.chris.wallet.api.integration.exchange.ExchangeRateResponse;
import com.chris.wallet.api.service.impl.RateExchangeServiceImpl;
import lombok.val;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RateExchangeServiceTest {

    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @Mock
    private WalletConfig walletConfig;

    private RateExchangeService underTest;

    private final static BigDecimal EUR_EXCHANGE_RATE = BigDecimal.valueOf(0.833324);
    private final static String EURO_CURRENCY = "EUR";
    private final static String USD_CURRENCY = "USD";

    @Before
    public void init() {
        underTest = new RateExchangeServiceImpl(exchangeRateApiClient, walletConfig);
    }

    @Test
    public void get_correct_rate_successfully() {
        //given
        val rate = getExchangeRateResponse();
        //when
        when(walletConfig.getAppId()).thenReturn("test_app_id");
        when(exchangeRateApiClient.getExchangeRate(walletConfig.getAppId())).thenReturn(rate);
        //then
        final Optional<BigDecimal> rateResolved = underTest.getExchangeRate(EURO_CURRENCY);
        Assert.assertEquals(Optional.of(EUR_EXCHANGE_RATE), rateResolved);
    }

    @Test
    public void get_exchange_rate_with_empty_api_id_should_return_empty_optional() {
        final Optional<BigDecimal> rateResolved = underTest.getExchangeRate(EURO_CURRENCY);
        Assert.assertEquals(Optional.empty(), rateResolved);
    }

    @Test
    public void get_exchange_rate_with_empty_rate_should_return_empty_optional() {
        final Optional<BigDecimal> rateResolved = underTest.getExchangeRate(null);
        Assert.assertEquals(Optional.empty(), rateResolved);
    }

    @Test
    public void get_exchange_rate_with_empty_rates_response_should_return_empty_optional() {
        final Optional<BigDecimal> rateResolved = underTest.getExchangeRate(null);
        Assert.assertEquals(Optional.empty(), rateResolved);
    }

    @Test
    public void get_empty_rates_from_third_party_expect_empty_optional() {
        //given
        val rate = getExchangeRateResponseWithoutRates();
        //when
        when(walletConfig.getAppId()).thenReturn("test_app_id");
        when(exchangeRateApiClient.getExchangeRate(walletConfig.getAppId())).thenReturn(rate);
        //then
        final Optional<BigDecimal> rateResolved = underTest.getExchangeRate(EURO_CURRENCY);
        Assert.assertEquals(Optional.empty(), rateResolved);
    }

    @Test(expected = ExchangeRateServiceUnavailableExceptions.class)
    public void exchange_rate_api_failure_should_throw_ExchangeRate_Service_Unavailable() {
        //when
        when(walletConfig.getAppId()).thenReturn("test_app_id");
        when(exchangeRateApiClient.getExchangeRate(walletConfig.getAppId())).thenThrow(new RuntimeException());
        //then
        underTest.getExchangeRate(EURO_CURRENCY);

    }


    @Test
    public void get_server_error_third_party_expect_empty_optional() {
        //given
        val rate = getExchangeRateResponseWithoutRates();
        //when
        when(walletConfig.getAppId()).thenReturn("test_app_id");
        when(exchangeRateApiClient.getExchangeRate(walletConfig.getAppId())).thenReturn(rate);
        //then
        final Optional<BigDecimal> rateResolved = underTest.getExchangeRate(EURO_CURRENCY);
        Assert.assertEquals(Optional.empty(), rateResolved);
    }


    private ResponseEntity<ExchangeRateResponse> getExchangeRateResponse() {
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put(USD_CURRENCY, BigDecimal.ONE);
        rates.put(EURO_CURRENCY, EUR_EXCHANGE_RATE);

        final ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
                                                                              .base(USD_CURRENCY)
                                                                              .disclaimer("")
                                                                              .license("")
                                                                              .rates(rates)
                                                                              .build();

        return new ResponseEntity<>(exchangeRateResponse, HttpStatus.OK);
    }

    private ResponseEntity<ExchangeRateResponse> getExchangeRateResponseWithoutRates() {

        final ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
                                                                              .base(USD_CURRENCY)
                                                                              .disclaimer("")
                                                                              .license("")
                                                                              .build();

        return new ResponseEntity<>(exchangeRateResponse, HttpStatus.OK);
    }


}
