/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildScheduledEventUserEvent extends Event {

    private final @NotNull GuildScheduledEventUserAddRemoveData data;
    private final boolean added;
    private final @Nullable GuildScheduledEvent scheduledEvent;

    public GuildScheduledEventUserEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull GuildScheduledEventUserAddRemoveData data, boolean added, @Nullable GuildScheduledEvent scheduledEvent) {
        super(lApi, payload, data.getGuildIdAsSnowflake());
        this.data = data;
        this.added = added;
        this.scheduledEvent = scheduledEvent;
    }

    /**
     * @return {@code true} if this is a user added event
     */
    public boolean isAdded() {
        return added;
    }

    /**
     * @return {@code true} if this is a user removed event
     */
    public boolean isRemoved() {
        return !added;
    }

    /**
     * @return The user that was {@link #isAdded() added or removed}
     */
    public @NotNull String getUserId(){
        return data.getUserId();
    }

    public @NotNull String getGuildScheduledEventId(){
        return data.getScheduledEventId();
    }

    /**
     * @return the cached {@link GuildScheduledEvent} or {@code null} if {@link ConfigFlag#CACHE_GUILD_SCHEDULED_EVENTS} is not enabled.
     */
    public @Nullable GuildScheduledEvent getGuildScheduledEvent() {
        return scheduledEvent;
    }


}
