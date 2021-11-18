package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.queue.Container;
import me.linusdev.discordbotapi.api.communication.queue.Error;
import me.linusdev.discordbotapi.api.communication.queue.Queueable;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class Retriever<T> implements Queueable<T>, HasLApi {
    protected final @NotNull LApi lApi;
    protected final @NotNull Query query;

    public Retriever(@NotNull LApi lApi, Query query){
        this.lApi = lApi;
        this.query = query;
    }

    public @NotNull Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException{
        return lApi.sendLApiHttpRequest(query.getLApiRequest());
    }

    public @Nullable abstract T retrieve() throws LApiException, IOException, ParseException, InterruptedException;

    @Override
    public @NotNull Container<T> completeHere() {
        Container<T> container;
        try {
            container = new Container<T>(retrieve(), null);
        } catch (LApiException | IOException | ParseException | InterruptedException e) {
            container = new Container<T>(null, new Error(e));
        }
        return container;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
