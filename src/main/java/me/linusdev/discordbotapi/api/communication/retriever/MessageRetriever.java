package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.queue.Container;
import me.linusdev.discordbotapi.api.communication.queue.Error;
import me.linusdev.discordbotapi.api.communication.queue.Queueable;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.objects.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class MessageRetriever extends Retriever<Message> {

    public MessageRetriever(@NotNull LApi lApi, @NotNull String channelId, @NotNull String messageId) {
        super(lApi, new GetLinkQuery(lApi, GetLinkQuery.Links.GET_CHANNEL_MESSAGE,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId),
                new PlaceHolder(PlaceHolder.MESSAGE_ID, messageId)));
    }


    @Override
    public @Nullable Message retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        return new Message(lApi, retrieveData());
    }
}
