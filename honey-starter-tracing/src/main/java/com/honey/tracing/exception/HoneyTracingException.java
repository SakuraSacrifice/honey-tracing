package com.honey.tracing.exception;

public class HoneyTracingException extends RuntimeException {

    public HoneyTracingException() {

    }

    public HoneyTracingException(String message) {
        super(message);
    }

    public HoneyTracingException(String message, Throwable cause) {
        super(message, cause);
    }

    public HoneyTracingException(Throwable cause) {
        super(cause);
    }

    public HoneyTracingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}