package me.linusdev.discordbotapi.api.communication.exceptions;

/**
 * file type is not supported I guess...
 */
public class UnsupportedFileTypeException extends LApiUncheckedException{
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
