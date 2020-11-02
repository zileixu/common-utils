package com.lmnplace.commonutils.exception;

public class NoInitException extends RuntimeException {
    public NoInitException(String message) {
        super(message);
    }

    public NoInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoInitException(Throwable cause) {
        super(cause);
    }

    protected NoInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
