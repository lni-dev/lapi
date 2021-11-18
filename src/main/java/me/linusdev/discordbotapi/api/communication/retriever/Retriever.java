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

/**
 * A Retriever is a {@link Queueable} that can be {@link LApi#queue(Queueable) queued}.
 * It is used to retrieve an Object from Discords endpoint ({@link MessageRetriever for example a Message}).
 * @param <T> the class of the Object that should be retrieved
 */
public abstract class Retriever<T> implements Queueable<T>, HasLApi {
    protected final @NotNull LApi lApi;
    protected final @NotNull Query query;

    /**
     *
     * @param lApi {@link LApi}
     * @param query the {@link Query} used to retrieve the {@link Data}
     */
    public Retriever(@NotNull LApi lApi, @NotNull Query query){
        this.lApi = lApi;
        this.query = query;
    }

    /**
     * Retrieves the wanted Objects JSON and saves it into a {@link Data}.<br>
     * You are probably looking for {@link Queueable#queue()} or {@link #completeHere()}
     *
     * @return {@link Data} with all JSON fields
     * @see Queueable#queue()
     * @see #completeHere()
     */
    public @NotNull Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException{
        return lApi.sendLApiHttpRequest(query.getLApiRequest());
    }

    /**
     * Retrieves the Object. This will happen on the current Thread! <br>
     * I suggest you use {@link Queueable#queue()} or {@link #completeHere()} instead!
     * @return retrieved Object
     * @see Queueable#queue()
     * @see #completeHere()
     */
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
