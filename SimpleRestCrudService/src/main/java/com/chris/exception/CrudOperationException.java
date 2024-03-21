package com.chris.exception;

public class CrudOperationException extends RuntimeException{
    public CrudOperationException(String message) { super(message); }
    public CrudOperationException(String message, Throwable cause) { super(message, cause); }
    public CrudOperationException(Throwable cause) { super(cause); }
    public CrudOperationException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
