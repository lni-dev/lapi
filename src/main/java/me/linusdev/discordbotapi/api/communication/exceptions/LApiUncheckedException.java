package me.linusdev.discordbotapi.api.communication.exceptions;

/**
 * Exceptions like this are thrown, if an Exception is thrown in some corner cases, but
 * catching it every time would be a hassle
 */
public class LApiUncheckedException extends RuntimeException {
    public LApiUncheckedException() {
    }

    public LApiUncheckedException(String message) {
        super(message);
    }

    public LApiUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LApiUncheckedException(Throwable cause) {
        super(cause);
    }

    public LApiUncheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
