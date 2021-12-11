package me.linusdev.discordbotapi.api.communication.gateway.events;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Event implements HasLApi {

    protected final @NotNull LApi lApi;

    protected final @Nullable Snowflake guildId;
    protected final @NotNull GatewayPayloadAbstract payload;

    public Event(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @Nullable Snowflake guildId) {
        this.lApi = lApi;
        this.guildId = guildId;
        this.payload = payload;
    }

    /**
     * If this event happened in a guild (server), this is the guild-id for this guild.<br>
     * If this event did not happen in a guild (server), this will be {@code null}
     *
     * @return {@link Snowflake} or {@code null}
     */
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * If this event is associated with a guild (server), this is the guild-id for this guild.<br>
     * If this event is not associated with a guild (server), this will be {@code null}
     *
     * @return {@link String} or {@code null}
     */
    public @Nullable String getGuildId() {
        if(guildId == null) return null;
        return guildId.asString();
    }

    /**
     *
     * @return {@link true} if this event is associated with a guild (server), {@link false} otherwise
     */
    public boolean isGuildEvent(){
        return getGuildIdAsSnowflake() != null;
    }


    /**
     * This is the {@link GatewayPayloadAbstract payload} received from Discord. You usually do not need this.
     * @return {@link GatewayPayloadAbstract}
     */
    public @NotNull GatewayPayloadAbstract getPayload() {
        return payload;
    }

    /**
     * The type which Discord send us with the event
     * @return {@link GatewayEvent}
     */
    public @Nullable GatewayEvent getEventType() {
        return payload.getType();
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
