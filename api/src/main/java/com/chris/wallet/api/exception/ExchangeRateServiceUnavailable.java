package com.chris.wallet.api.exception;

public class ExchangeRateServiceUnavailable extends WalletException {
    private static final long serialVersionUID = -1803010479107053560L;

    public ExchangeRateServiceUnavailable(String errorCause) {
        super(-6000, errorCause);
    }
}
