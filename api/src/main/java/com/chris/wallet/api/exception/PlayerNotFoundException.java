package com.chris.wallet.api.exception;

public class PlayerNotFoundException extends WalletException {
    private static final long serialVersionUID = -5026198225069438512L;

    public PlayerNotFoundException() {
        super(-1, "PlayerNotFound");
    }
}
