package me.linusdev.discordbotapi.api.communication.gateway.events.guild.emoji;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildEmojisUpdateEvent extends Event implements GuildEvent {

    public final Data updateData;

    public GuildEmojisUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId, Data updateData) {
        super(lApi, payload, guildId);
        //TODO:
        this.updateData = updateData;
    }
}
