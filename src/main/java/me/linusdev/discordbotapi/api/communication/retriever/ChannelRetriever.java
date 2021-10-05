package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ChannelRetriever extends Retriever{

    private final @NotNull String id;
    private final GetLinkQuery query;

    public ChannelRetriever(@NotNull LApi lApi, @NotNull String id){
        super(lApi);
        this.id = id;

        this.query = new GetLinkQuery(lApi, GetLinkQuery.Links.GET_CHANNEL, new PlaceHolder(PlaceHolder.CHANNEL_ID, id));
    }

    public Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException {
        return lApi.sendLApiHttpRequest(query.getLApiRequest());
    }

    public Channel retrieveChannel() throws LApiException, IOException, ParseException, InterruptedException {
        Data data = retrieveData();
        return Channel.fromData(lApi, data);
    }
}
