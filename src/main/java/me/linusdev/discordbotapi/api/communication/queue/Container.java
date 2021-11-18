package me.linusdev.discordbotapi.api.communication.queue;

import org.jetbrains.annotations.Nullable;

/**
 * This is a simple Container for an {@link #object} and an {@link #error}.
 * This is mainly used by {@link Future}, which cannot throw an exception, because it
 * is running in a different thread.
 * @param <T> type of {@link #object}
 */
public class Container<T> {

    private @Nullable final T object;
    private @Nullable final Error error;

    public Container(@Nullable T object, @Nullable Error error){
        this.object = object;
        this.error = error;
    }

    /**
     * @return {@code false} of {@link #getError()} is {@code null}, {@code true} otherwise
     */
    public boolean hasError(){
        return error != null;
    }

    /**
     *
     * @return The contained {@link #object}. Can be {@code null}
     */
    public @Nullable T get() {
        return object;
    }

    /**
     *
     * @return The contained {@link #error}. Can be {@code null}
     */
    public @Nullable Error getError() {
        return error;
    }
}
