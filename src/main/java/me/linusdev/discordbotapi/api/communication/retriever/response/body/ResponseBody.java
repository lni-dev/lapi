package me.linusdev.discordbotapi.api.communication.retriever.response.body;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

/**
 * This is returned by some {@link me.linusdev.discordbotapi.api.communication.retriever.Retriever retrievers}, if they retrieve more than one object
 */
public abstract class ResponseBody implements HasLApi {

    protected final @NotNull LApi lApi;
    protected final @NotNull Data data;

    /**
     *
     * @param lApi {@link LApi}
     * @param data the retrieved {@link Data}
     */
    public ResponseBody(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        this.data = data;
        this.lApi = lApi;
    }

    /**
     * the retrieved {@link Data}
     */
    public @NotNull Data getData() {
        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
