package me.linusdev.discordbotapi.api.communication.queue;

import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @param <T> Type of the data that should be retrieved
 */
public interface Queueable<T> extends HasLApi {
    //TODO

    /**
     * queues this {@link Queueable} and returns a {@link Future}<br>
     * see {@link Future} for more information!
     * @see Future
     */
    default @NotNull Future<T> queue(){
        return getLApi().queue(this);
    }

    /**
     * Complete this {@link Queueable<T>} in this Thread.
     */
    @NotNull Container<T> completeHere();

}
