package com.lmnplace.commonutils.exception;

public class ConfParamsValidationException extends  RuntimeException {
    public ConfParamsValidationException(String message) {
        super(message);
    }

    public ConfParamsValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfParamsValidationException(Throwable cause) {
        super(cause);
    }

    protected ConfParamsValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
