package com.chris.exception;

/**
 * exception used for the chris auth service
 */
public class AuthServiceException extends RuntimeException {
    public AuthServiceException(String message) {
        super(message);
    }

    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthServiceException(Throwable cause) {
        super(cause);
    }

    public AuthServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
