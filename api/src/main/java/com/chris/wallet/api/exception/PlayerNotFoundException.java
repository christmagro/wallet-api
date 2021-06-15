package com.chris.wallet.api.exception;

public class PlayerNotFoundException extends WalletException {
    public PlayerNotFoundException() {
        super(-1, "PlayerNotFound");
    }
}
