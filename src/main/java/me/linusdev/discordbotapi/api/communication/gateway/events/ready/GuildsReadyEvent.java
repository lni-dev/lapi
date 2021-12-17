package me.linusdev.discordbotapi.api.communication.gateway.events.ready;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.manager.guild.GuildPool;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildsReadyEvent extends Event {

    protected final GuildPool guildPool;

    public GuildsReadyEvent(@NotNull LApi lApi, @NotNull GuildPool guildPool) {
        super(lApi, null, null);
        this.guildPool = guildPool;
    }

    public GuildPool getGuildPool() {
        return guildPool;
    }
}
