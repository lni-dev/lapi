package me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.message.MessageImplementation;
import me.linusdev.discordbotapi.api.objects.message.abstracts.Message;
import org.jetbrains.annotations.NotNull;


public class MessageCreateEvent extends Event {

    protected final @NotNull MessageImplementation message;

    public MessageCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull MessageImplementation message) {
        super(lApi, payload, message.getGuildIdAsSnowflake());
        this.message = message;
    }

    /**
     * The message that was created.
     *
     * @return {@link Message}
     */
    public @NotNull Message getMessage() {
        return message;
    }

    /**
     *
     * @return the channel-id of the channel, the message was sent in
     */
    public @NotNull String getChannelId() {
        return message.getChannelId();
    }
}
