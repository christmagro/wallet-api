package com.chris.wallet.api.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class WalletException extends RuntimeException {

    private static final long serialVersionUID = -3214563654732027776L;

    @Getter
    protected int returnCode;

    @Getter
    protected String errorCause;

    public WalletException(int returnCode, String errorCause) {
        this.returnCode = returnCode;
        this.errorCause = errorCause;
        log.error("Error Code:{} and Error Cause:{}", returnCode, errorCause);
    }
}
