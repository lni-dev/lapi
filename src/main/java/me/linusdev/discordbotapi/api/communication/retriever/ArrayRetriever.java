package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.converter.Converter;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * This class is used to retrieve any JSON Array and convert it to an {@link ArrayList} of type {@link R}
 * @param <C> the class which will be retrieved from the Discord Api, usually {@link Data}
 * @param <R> the result class, to which {@link C} should be converted to
 */
public class ArrayRetriever<C, R> extends Retriever<ArrayList<R>>{

    private final @NotNull Converter<C, R> converter;

    /**
     *
     * @param lApi {@link LApi}
     * @param query {@link Query} for the HttpRequest
     * @param converter {@link Function} to convert from {@link C} to {@link R}
     */
    public ArrayRetriever(@NotNull LApi lApi, @NotNull Query query, @NotNull Converter<C, R> converter) {
        super(lApi, query);
        this.converter = converter;
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public @Nullable ArrayList<R> retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        ArrayList<Object> dataArray = (ArrayList<Object>) retrieveData().get("array");
        ArrayList<R> resultArray = new ArrayList<>(dataArray.size());

        for(Object o : dataArray)
            resultArray.add(converter.apply(lApi, (C) o));

        return resultArray;
    }

    @Override
    public @NotNull Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException {
        return lApi.sendLApiHttpRequest(query.getLApiRequest(), "array");
    }
}
