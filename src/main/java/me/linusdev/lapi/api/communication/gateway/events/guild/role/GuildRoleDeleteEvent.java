/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.communication.gateway.events.guild.role;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
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
