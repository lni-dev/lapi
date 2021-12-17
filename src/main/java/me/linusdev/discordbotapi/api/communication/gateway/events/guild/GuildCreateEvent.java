package me.linusdev.discordbotapi.api.communication.gateway.events.guild;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.Guild;
import me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild;
import me.linusdev.discordbotapi.api.objects.guild.UpdatableGuildAbstract;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildCreateEvent extends Event implements GuildEvent {

    protected final @NotNull UpdatableGuild guild;

    public GuildCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull UpdatableGuild guild) throws InvalidDataException {
        super(lApi, payload, guild.getIdAsSnowflake());
        this.guild = guild;

    }

    public @NotNull UpdatableGuildAbstract getGuild() {
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
