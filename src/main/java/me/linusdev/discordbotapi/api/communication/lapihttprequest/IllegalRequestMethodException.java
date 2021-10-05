package me.linusdev.discordbotapi.api.communication.lapihttprequest;

public class IllegalRequestMethodException extends LApiHttpRequestException{
    public IllegalRequestMethodException() {
    }

    public IllegalRequestMethodException(String message) {
        super(message);
    }

    public IllegalRequestMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
