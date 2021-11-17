package me.linusdev.discordbotapi.api.communication.queue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Error {

    private @Nullable Throwable throwable;

    public Error(@Nullable Throwable t){
        this.throwable = t;
    }

    public @Nullable Throwable getThrowable() {
        return throwable;
    }
}
