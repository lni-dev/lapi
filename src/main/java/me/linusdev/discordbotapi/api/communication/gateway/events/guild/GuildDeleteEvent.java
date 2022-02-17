package me.linusdev.discordbotapi.api.communication.gateway.events.guild;

import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuild;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildDeleteEvent extends Event implements GuildEvent {

    protected final @Nullable CachedGuildImpl guild;

    public GuildDeleteEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull CachedGuildImpl guild) throws InvalidDataException {
        super(lApi, payload, guild.getIdAsSnowflake());
        this.guild = guild;
    }

    public GuildDeleteEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull String guildId) throws InvalidDataException {
        super(lApi, payload, Snowflake.fromString(guildId));
        this.guild = null;
    }

    /**
     * Only available if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is enabled.
     * @return The deleted {@link CachedGuild cached guild}, which was now removed.
     */
    public @Nullable CachedGuild getCachedGuild() {
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
