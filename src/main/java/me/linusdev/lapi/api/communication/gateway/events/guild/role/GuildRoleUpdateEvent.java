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
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
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
     * This will not be {@code null} if {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_ROLE_ON_UPDATE_EVENT} is enabled.
     * @return copy of the {@link Role} before it was updated.
     */
    public @Nullable Role getOldRole(){
        return role.getCopy();
    }
}
