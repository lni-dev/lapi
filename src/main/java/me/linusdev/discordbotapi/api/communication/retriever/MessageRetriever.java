package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.objects.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * This class is used to retrieve a {@link Message}.<br>
 * <br>
 * Example:<br>
 * <pre>{@code
 * LApi api = new LApi(TOKEN, config);
 * MessageRetriever retriever = new MessageRetriever(api, "820084724693073921",
 *                                      "854807543603003402");
 *
 * retriever.queue().then((message, error) -> {
 *             if(error != null){
 *                 error.getThrowable().printStackTrace();
 *                 return;
 *             }
 *             System.out.println("Message Content: " + message.getContent());
 *         });
 * }
 * </pre>
 *
 *
 * @see Message
 */
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
