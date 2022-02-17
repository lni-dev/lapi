package me.linusdev.discordbotapi.api.communication.gateway.events.guild;

import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;

public class GuildUnavailableEvent extends Event implements GuildEvent {

    protected final @NotNull CachedGuildImpl guild;

    public GuildUnavailableEvent(@NotNull LApi lApi, @NotNull CachedGuildImpl guild) {
        super(lApi, null, guild.getIdAsSnowflake());
        this.guild = guild;
    }

    public @NotNull CachedGuildImpl getGuild() {
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
