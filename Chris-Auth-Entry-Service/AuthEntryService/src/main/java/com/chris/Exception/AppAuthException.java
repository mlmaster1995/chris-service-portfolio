package com.chris.Exception;

/**
 * exception used for the chris auth service
 */
public class AppAuthException extends RuntimeException {
    public AppAuthException(String message) {
        super(message);
    }

    public AppAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppAuthException(Throwable cause) {
        super(cause);
    }

    public AppAuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
