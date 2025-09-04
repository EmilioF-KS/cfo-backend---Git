package com.cfo.reporting.exception;

public class DataScreenProcessingException extends Exception{
    public DataScreenProcessingException() {
    }

    public DataScreenProcessingException(String message) {
        super(message);
    }

    public DataScreenProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataScreenProcessingException(Throwable cause) {
        super(cause);
    }

    public DataScreenProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
