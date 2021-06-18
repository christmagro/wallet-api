package com.chris.wallet.api.exception;

public class TransactionAlreadyExistsException extends WalletException {


    private static final long serialVersionUID = 6146145252851821662L;

    public TransactionAlreadyExistsException(String message) {
        super(-10, String.format("Transaction ID: %s already exists", message));
    }
}
