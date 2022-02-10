package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.objects.message.MessageImplementation;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.O_DISCORD_API_VERSION_LINK;
import static me.linusdev.discordbotapi.api.communication.PlaceHolder.*;
import static me.linusdev.discordbotapi.api.communication.lapihttprequest.Method.*;

/**
 * These are links to communicate with the official discord api.
 * Some more links are still in {@link GetLinkQuery.Links}
 */
public enum Link implements AbstractLink{

    /**
     * Post a message to a guild text or DM channel. Returns a {@link MessageImplementation message} object.
     *
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#create-message" target="_top">Create Message</a>
     */
    CREATE_MESSAGE(POST, O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/messages"),

    /**
     * Returns an object with a single valid WSS URL, which the client can use for Connecting.
     * Clients should cache this value and only call this endpoint to retrieve a new URL if
     * they are unable to properly establish a connection using the cached version of the URL.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway" target="_top">Get Gateway</a>
     */
    GET_GATEWAY(GET, O_DISCORD_API_VERSION_LINK + "gateway"),

    /**
     * Returns an object based on the information in Get Gateway,
     * plus additional metadata that can help during the operation of large or sharded bots.
     * Unlike the Get Gateway, this route should not be cached for extended periods of time
     * as the value is not guaranteed to be the same per-call, and changes as the bot joins/leaves
     * guilds.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway-bot" target="_top">Get Gateway Bot</a>
     */
    GET_GATEWAY_BOT(GET, O_DISCORD_API_VERSION_LINK + "gateway/bot"),

    GET_VOICE_REGIONS(GET, O_DISCORD_API_VERSION_LINK + "voice/regions"),
    ;

    private final @NotNull Method method;
    private final @NotNull String link;

    Link(@NotNull Method method, @NotNull String link){
        this.method = method;
        this.link = link;
    }

    @Override
    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @NotNull String getLink() {
        return link;
    }
}
