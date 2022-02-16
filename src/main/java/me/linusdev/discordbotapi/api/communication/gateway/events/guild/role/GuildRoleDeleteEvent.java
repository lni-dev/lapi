package me.linusdev.discordbotapi.api.communication.gateway.events.guild.role;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildRoleDeleteEvent extends Event implements GuildEvent {

    private final @Nullable Role role;
    private final @NotNull Snowflake roleId;

    public GuildRoleDeleteEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull String guildId, @NotNull Role role) {
        super(lApi, payload, Snowflake.fromString(guildId));

        this.role = role;
        this.roleId = role.getIdAsSnowflake();
    }

    public GuildRoleDeleteEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull String guildId, @NotNull String roleId) {
        super(lApi, payload, Snowflake.fromString(guildId));

        this.role = null;
        this.roleId = Snowflake.fromString(roleId);
    }

    public @Nullable Role getRole() {
        return role;
    }

    public @NotNull Snowflake getRoleIdAsSnowflake() {
        return roleId;
    }

    public @NotNull String getRoleId(){
        return roleId.asString();
    }
}
