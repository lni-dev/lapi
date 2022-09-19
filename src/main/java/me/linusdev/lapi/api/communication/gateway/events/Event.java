/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.communication.gateway.events;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Event implements HasLApi {

    protected final @NotNull LApi lApi;

    protected final @Nullable Snowflake guildId;
    protected final @Nullable GatewayPayloadAbstract payload;

    public Event(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId) {
        this.lApi = lApi;
        this.guildId = guildId;
        this.payload = payload;
    }

    /**
     * If this event happened in a guild (server), this is the guild-id for this guild.<br>
     * If this event did not happen in a guild (server), this will be {@code null}
     *
     * @return {@link Snowflake} or {@code null}
     */
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * If this event is associated with a guild (server), this is the guild-id for this guild.<br>
     * If this event is not associated with a guild (server), this will be {@code null}
     *
     * @return {@link String} or {@code null}
     */
    public @Nullable String getGuildId() {
        if(guildId == null) return null;
        return guildId.asString();
    }

    /**
     *
     * @return {@code true} if this event is associated with a guild (server), {@code false} otherwise
     */
    public boolean isGuildEvent(){
        return getGuildIdAsSnowflake() != null;
    }


    /**
     * This is the {@link GatewayPayloadAbstract payload} received from Discord. You usually do not need this.
     * @return {@link GatewayPayloadAbstract}
     */
    public @Nullable GatewayPayloadAbstract getPayload() {
        return payload;
    }

    /**
     * The type which Discord send us with the event
     * @return {@link GatewayEvent}
     */
    public @Nullable GatewayEvent getEventType() {
        return payload.getType();
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
