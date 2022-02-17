package me.linusdev.discordbotapi.api.communication.gateway.events.guild.role;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.communication.gateway.update.Update;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildRoleUpdateEvent extends Event implements GuildEvent {

    private final @NotNull Update<Role, Role> role;

    public GuildRoleUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull Snowflake guildId, @NotNull Update<Role, Role> role) {
        super(lApi, payload, guildId);

        this.role = role;
    }

    public @NotNull Role getRole() {
        return role.getObj();
    }

    /**
     * This will not be {@code null} if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#COPY_ROLE_ON_UPDATE_EVENT} is enabled.
     * @return copy of the {@link Role} before it was updated.
     */
    public @Nullable Role getOldRole(){
        return role.getCopy();
    }
}
