package com.chris.wallet.api.exception;

public class UsernameAlreadyExistsException extends WalletException {
    private static final long serialVersionUID = 4083124283766757236L;

    public UsernameAlreadyExistsException(String username) {
        super(-900, String.format("Username: [%s] already exists", username));
    }
}
