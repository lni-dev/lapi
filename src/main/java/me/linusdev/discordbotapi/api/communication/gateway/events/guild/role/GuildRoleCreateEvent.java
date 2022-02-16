package me.linusdev.discordbotapi.api.communication.gateway.events.guild.role;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildRoleCreateEvent extends Event implements GuildEvent {

    private final @NotNull Role role;

    public GuildRoleCreateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull Snowflake guildId, @NotNull Role role) {
        super(lApi, payload, guildId);

        this.role = role;
    }

    public @NotNull Role getRole() {
        return role;
    }
}
