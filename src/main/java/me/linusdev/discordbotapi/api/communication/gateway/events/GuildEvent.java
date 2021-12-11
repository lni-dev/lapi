package me.linusdev.discordbotapi.api.communication.gateway.events;

import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;

public interface GuildEvent {

    /**
     * This is the guild-id for the associated guild (server).<br>
     *
     * @return {@link Snowflake}
     */
    @NotNull Snowflake getGuildIdAsSnowflake();


    /**
     * This is the guild-id for the associated guild (server).<br>
     *
     * @return {@link String}
     */
    default @NotNull String getGuildId() {
        return getGuildIdAsSnowflake().asString();
    }
}
