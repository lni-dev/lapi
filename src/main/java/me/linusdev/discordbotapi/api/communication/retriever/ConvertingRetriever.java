package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.converter.Converter;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 *
 * This retriever is used to retrieve a JSON as {@link Data} from Discord and convert to it {@link R}
 *
 * @param <R> the result class, the {@link Data} should be converted to
 */
public class ConvertingRetriever<R> extends Retriever<R>{

    private final Converter<Data, R> converter;

    /**
     *
     * @param lApi {@link LApi}
     * @param query {@link Query} for the HttpRequest
     * @param converter {@link Converter} to convert from {@link Data} to {@link R}
     */
    public ConvertingRetriever(@NotNull LApi lApi, @NotNull Query query, @NotNull Converter<Data, R> converter) {
        super(lApi, query);
        this.converter = converter;
    }

    @Override
    public @Nullable R retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        return converter.convert(lApi, retrieveData());
    }
}
