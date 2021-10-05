package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.discordbotapi.api.LApi;
import org.jetbrains.annotations.NotNull;

public abstract class Retriever {
    protected final @NotNull LApi lApi;

    public Retriever(@NotNull LApi lApi){
        this.lApi = lApi;
    }
}
