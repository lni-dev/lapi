package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.communication.retriever.query.SimpleGetLinkQuery;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class ArrayRetriever extends Retriever{

    private Query query;

    public ArrayRetriever(@NotNull LApi lApi, Query query) {
        super(lApi);
        this.query = query;
    }

    public ArrayList retrieveArray() throws LApiException, IOException, ParseException, InterruptedException {
        Data data = lApi.sendLApiHttpRequest(query.getLApiRequest(), "array");
        return (ArrayList) data.get("array");
    }
}
