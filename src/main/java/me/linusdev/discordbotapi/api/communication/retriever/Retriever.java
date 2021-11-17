package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

public abstract class Retriever implements HasLApi {
    protected final @NotNull LApi lApi;

    public Retriever(@NotNull LApi lApi){
        this.lApi = lApi;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
