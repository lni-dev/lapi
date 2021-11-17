package me.linusdev.discordbotapi.api.communication.queue;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface Queueable<T> extends HasLApi {
    //TODO

    default @NotNull Future<T> queue(){
        return getLApi().queue(this);
    }

    @NotNull Container<T> completeHere();

}
