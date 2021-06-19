package com.chris.wallet.api.exception;

public class InvalidExchangeRateException extends WalletException {
    private static final long serialVersionUID = -7958891776392262422L;

    public InvalidExchangeRateException() {
        super(-1008, "Currency invalid or not supported");
    }
}
