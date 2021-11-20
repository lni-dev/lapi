package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * This class is used to retrieve a {@link Channel}.
 * You might want to cast this {@link Channel} depending on its {@link Channel#getType() type}
 * @see Channel
 * @see me.linusdev.discordbotapi.api.objects.enums.ChannelType
 */
public class ChannelRetriever extends Retriever<Channel>{

    public ChannelRetriever(@NotNull LApi lApi, @NotNull String id){
        super(lApi, new GetLinkQuery(lApi, GetLinkQuery.Links.GET_CHANNEL, new PlaceHolder(PlaceHolder.CHANNEL_ID, id)));
    }

    @Override
    public @Nullable Channel retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        return Channel.fromData(lApi, retrieveData());
    }
}
