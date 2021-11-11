package me.linusdev.discordbotapi.api.objects.message.embed;

import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;

/**
 * This
 */
public class InvalidEmbedException extends LApiException {

    public InvalidEmbedException(String message){
        super(message);
    }
}
