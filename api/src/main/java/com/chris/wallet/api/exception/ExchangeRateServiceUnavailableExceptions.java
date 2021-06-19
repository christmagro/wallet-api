package com.chris.wallet.api.exception;

public class ExchangeRateServiceUnavailableExceptions extends WalletException {
    private static final long serialVersionUID = -1803010479107053560L;

    public ExchangeRateServiceUnavailableExceptions(String errorCause) {
        super(-6000, errorCause);
    }
}
