package me.linusdev.discordbotapi.api.communication.gateway.events.guild;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildUnavailableEvent extends Event implements GuildEvent {

    protected final @NotNull UpdatableGuild guild;

    public GuildUnavailableEvent(@NotNull LApi lApi, @NotNull UpdatableGuild guild) {
        super(lApi, null, guild.getIdAsSnowflake());
        this.guild = guild;
    }

    public @NotNull UpdatableGuild getGuild() {
        return guild;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return super.getGuildIdAsSnowflake();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull String getGuildId() {
        return super.getGuildId();
    }
}
