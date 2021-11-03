package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MessageRetriever extends Retriever{

    private final @NotNull Query query;

    public MessageRetriever(@NotNull LApi lApi, @NotNull String channelId, @NotNull String messageId) {
        super(lApi);
        this.query = new GetLinkQuery(lApi, GetLinkQuery.Links.GET_CHANNEL_MESSAGE,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId),
                new PlaceHolder(PlaceHolder.MESSAGE_ID, messageId));
    }

    public Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException {
        return lApi.sendLApiHttpRequest(query.getLApiRequest());
    }
}
