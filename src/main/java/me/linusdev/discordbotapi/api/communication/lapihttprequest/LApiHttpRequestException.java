package me.linusdev.discordbotapi.api.communication.lapihttprequest;

import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;

public class LApiHttpRequestException extends LApiException {
    public LApiHttpRequestException() {
    }

    public LApiHttpRequestException(String message) {
        super(message);
    }

    public LApiHttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
