package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used to retrieve any JSON Array
 */
public class ArrayRetriever extends Retriever<ArrayList<Object>>{

    public ArrayRetriever(@NotNull LApi lApi, Query query) {
        super(lApi, query);
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public @Nullable ArrayList<Object> retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        return (ArrayList<Object>) retrieveData().get("array");
    }

    @Override
    public @NotNull Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException {
        return lApi.sendLApiHttpRequest(query.getLApiRequest(), "array");
    }
}
