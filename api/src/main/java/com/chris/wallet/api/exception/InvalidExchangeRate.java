package com.chris.wallet.api.exception;

public class InvalidExchangeRate extends WalletException {
    private static final long serialVersionUID = -7958891776392262422L;

    public InvalidExchangeRate() {
        super(-1008, "Currency invalid or not supported");
    }
}
