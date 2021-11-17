package me.linusdev.discordbotapi.api.communication.queue;

import org.jetbrains.annotations.Nullable;

public class Container<T> {

    private @Nullable T object;
    private @Nullable Error error;

    public Container(@Nullable T object, @Nullable Error error){
        this.object = object;
        this.error = error;
    }

    public boolean hasError(){
        return error != null;
    }

    public @Nullable T get() {
        return object;
    }

    public @Nullable Error getError() {
        return error;
    }
}
