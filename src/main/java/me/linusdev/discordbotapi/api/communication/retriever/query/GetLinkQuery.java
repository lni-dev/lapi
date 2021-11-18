package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;
import static me.linusdev.discordbotapi.api.communication.PlaceHolder.*;

public class GetLinkQuery implements Query{

    public enum Links{
        /**
         * Get a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel channel} by ID.
         * Returns a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel channel object}.
         * If the channel is a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread thread},
         * a {@link Thread#getMember() thread member} object is included in the returned result.
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel" target="_top">Get Channel</a>
         */
        GET_CHANNEL(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID),

        /**
         * <p>
         * Returns the messages for a channel. If operating on a guild channel,
         * this endpoint requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#VIEW_CHANNEL VIEW_CHANNEL}
         * permission to be present on the current user. If the current user is missing the
         * '{@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY}' permission in the channel
         * then this will return no messages (since they cannot read the message history).
         * Returns an array of {@link me.linusdev.discordbotapi.api.objects.message.Message message objects} on success.
         * </p>
         * <br>
         * TODO: This Requires Query String parameters (https://discord.com/developers/docs/resources/channel#get-channel-messages-query-string-params)
         * <p>
         *    The before, after, and around keys are mutually exclusive, only one may be passed at a time.
         * </p>
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-messages" target="_top">Get Channel Messages</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-messages-query-string-params" target="_top">Query String Params</a>
         */
        GET_CHANNEL_MESSAGES(O_DISCORD_API_VERSION_LINK + "/channels/" + CHANNEL_ID + "/messages/"),

        /**
         * <p>
         *     Returns a specific message in the channel. If operating on a guild channel,
         *     this endpoint requires the '{@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY}'
         *     permission to be present on the current user. Returns a {@link me.linusdev.discordbotapi.api.objects.message.Message message object} on success.
         * </p>
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see PlaceHolder#MESSAGE_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-message" target="_top">Get Channel Message</a>
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
