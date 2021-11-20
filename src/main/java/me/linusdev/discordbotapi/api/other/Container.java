package me.linusdev.discordbotapi.api.other;

import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

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

    /**
     *
     * @param converter {@link Function} to convert from {@link T} to {@link C}
     * @param <C> the Class to convert to
     * @return new {@link Container} of type C
     */
    public @NotNull <C> Container<C> convert(Function<T, C> converter){
        if(object == null) return new Container<C>(null, error);
        return new Container<C>(converter.apply(object), error);
    }
}
