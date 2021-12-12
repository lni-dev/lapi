package me.linusdev.discordbotapi.api.communication.exceptions;

public class LimitException extends LApiRuntimeException{

    public LimitException(String message){
        super(message);
    }
}
