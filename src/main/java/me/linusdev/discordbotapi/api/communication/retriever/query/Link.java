package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
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
     * Post a message to a guild text or DM channel. Returns a {@link me.linusdev.discordbotapi.api.objects.message.Message message} object.
     *
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#create-message" target="_top">Create Message</a>
     */
    CREATE_MESSAGE(POST, O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/messages"),
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
