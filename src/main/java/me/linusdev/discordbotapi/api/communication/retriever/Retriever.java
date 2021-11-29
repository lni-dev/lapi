package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.other.Container;
import me.linusdev.discordbotapi.api.other.Error;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * A Retriever is a {@link Queueable} that can be {@link LApi#queue(Queueable) queued}.
 * It is used to retrieve an Object from Discords endpoint ({@link MessageRetriever for example a Message}).
 * @param <T> the class of the Object that should be retrieved
 */
public abstract class Retriever<T> extends Queueable<T> implements HasLApi {
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
     * Retrieves the Object. This will happen on the current Thread! <br>
     * I suggest you use {@link Queueable#queue()} or {@link #completeHereAndIgnoreQueueThread()} instead!
     * @return retrieved Object
     * @see Queueable#queue()
     * @see #completeHereAndIgnoreQueueThread()
     */
    protected @Nullable abstract T retrieve() throws LApiException, IOException, ParseException, InterruptedException;

    @Override
    protected @NotNull Container<T> completeHereAndIgnoreQueueThread() {
        Container<T> container;
        try {
            container = new Container<T>(retrieve(), null);
        } catch (Throwable t) {
            LogInstance log = Logger.getLogger("Retriever", Logger.Type.ERROR);
            log.error("Exception while trying to retrieve " + query.toString());
            log.error(t);
            container = new Container<T>(null, new Error(t));
        }
        return container;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
