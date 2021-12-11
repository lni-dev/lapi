package me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.message.MessageImplementation;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildMessageCreateEvent extends MessageCreateEvent implements GuildEvent {

    public GuildMessageCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull MessageImplementation message) {
        super(lApi, payload, message);
    }

    public GuildMessageCreateEvent(MessageCreateEvent event){
        super(event.getLApi(), event.getPayload(), event.message);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        //Guild messages will have a message id
        return super.getGuildIdAsSnowflake();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull String getGuildId() {
        //Guild messages will have a message id
        return super.getGuildId();
    }
}
