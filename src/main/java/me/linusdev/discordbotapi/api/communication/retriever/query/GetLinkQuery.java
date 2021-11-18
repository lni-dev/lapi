package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;
import static me.linusdev.discordbotapi.api.communication.PlaceHolder.*;

public class GetLinkQuery implements Query{

    public enum Links{
        /**
         * Get a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel channel} by ID.
         * Returns a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel channel object}.
         * If the channel is a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread thread},
         * a thread member object is included in the returned result.
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel" target="_top">Get Channel</a>
         */
        GET_CHANNEL(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID),

        /**
         * @see PlaceHolder#CHANNEL_ID
         * @see PlaceHolder#MESSAGE_ID
         */
        GET_CHANNEL_MESSAGE(O_DISCORD_API_VERSION_LINK + "/channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID)
        ;

        private final String link;

        Links(String link){
            this.link = link;
        }

        public String getLink() {
            return link;
        }
    }

    private final LApi lApi;
    private final Links link;
    private final PlaceHolder[] placeHolders;

    public GetLinkQuery(@NotNull LApi lApi, @NotNull Links link, PlaceHolder... placeHolders){
        this.lApi = lApi;
        this.link = link;
        this.placeHolders = placeHolders;
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

    @Override
    public LApiHttpRequest getLApiRequest() throws LApiException {
        String uri = link.getLink();
        for(PlaceHolder p : placeHolders) uri = p.place(uri);

        return lApi.appendHeader(new LApiHttpRequest(uri, getMethod()));
    }
}
