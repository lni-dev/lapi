package me.linusdev.discordbotapi.api.communication.gateway.events.guild;

import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuild;
import me.linusdev.discordbotapi.api.objects.guild.Guild;
import me.linusdev.discordbotapi.api.objects.guild.GuildImpl;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildCreateEvent extends Event implements GuildEvent {

    protected final @Nullable CachedGuild cachedGuild;
    protected final @NotNull Guild guild;

    public GuildCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull Guild guild) throws InvalidDataException {
        super(lApi, payload, guild.getIdAsSnowflake());
        this.cachedGuild = null;
        this.guild = guild;
    }

    public GuildCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull CachedGuild guild) throws InvalidDataException {
        super(lApi, payload, guild.getIdAsSnowflake());
        this.cachedGuild = guild;
        this.guild = guild;

    }

    /**
     * The {@link CachedGuild} associated with this event. <br>
     * Will be {@code null} if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is disabled
     */
    public @Nullable CachedGuild getCachedGuild() {
        return cachedGuild;
    }

    /**
     * This will be a {@link Guild temporary guild object} if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is disabled
     * or a {@link CachedGuild cached guild object} if it is enabled.
     */
    public @NotNull Guild getGuild() {
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
