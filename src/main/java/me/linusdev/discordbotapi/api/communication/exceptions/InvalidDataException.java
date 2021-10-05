package me.linusdev.discordbotapi.api.communication.exceptions;

public class InvalidDataException extends LApiException{
    public InvalidDataException() {
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
