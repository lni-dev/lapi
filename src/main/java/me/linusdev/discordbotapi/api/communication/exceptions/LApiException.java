package me.linusdev.discordbotapi.api.communication.exceptions;

public class LApiException extends Exception{

    public LApiException() {
    }

    public LApiException(String message) {
        super(message);
    }

    public LApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
