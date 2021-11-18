package me.linusdev.discordbotapi.api.communication.queue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is an Error Object that contains a {@link Throwable}.<br>
 * This is used if an Exception cannot be thrown for whatever reason.<br>
 * For example in {@link Future}
 */
public class Error {
    private @Nullable final Throwable throwable;

    public Error(@Nullable Throwable t){
        this.throwable = t;
    }

    /**
     *
     * @return the {@link Throwable}, most likely an {@link Exception}
     */
    public @Nullable Throwable getThrowable() {
        return throwable;
    }
}
