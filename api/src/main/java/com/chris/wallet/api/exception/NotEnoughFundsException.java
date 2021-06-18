package com.chris.wallet.api.exception;

public class NotEnoughFundsException extends WalletException {
    private static final long serialVersionUID = -7308209268710846331L;

    public NotEnoughFundsException() {
        super(-10, "Not enough funds");
    }
}
