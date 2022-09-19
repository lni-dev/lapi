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
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildScheduledEventEvent extends Event {

    public enum Type {
        CREATE,
        UPDATE,
        DELETE,
    }

    private final @NotNull Type type;
    private final @NotNull Update<GuildScheduledEvent, GuildScheduledEvent> scheduledEvent;

    public GuildScheduledEventEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull Type type,
                                    @NotNull GuildScheduledEvent scheduledEvent) {
        super(lApi, payload, scheduledEvent.getGuildIdAsSnowflake());
        this.type = type;
        this.scheduledEvent = new Update<>(null, scheduledEvent);
    }

    public GuildScheduledEventEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull Type type,
                                    @NotNull Update<GuildScheduledEvent, GuildScheduledEvent> update) {
        super(lApi, payload, update.getObj().getGuildIdAsSnowflake());
        this.type = type;
        this.scheduledEvent = update;
    }


    /**
     * whether this is a {@link Type#CREATE}, {@link Type#UPDATE} or {@link Type#DELETE}
     * @return {@link Type} of this event.
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     *
     * @return the {@link GuildScheduledEvent}
     */
    public @NotNull GuildScheduledEvent getGuildScheduledEvent() {
        return scheduledEvent.getObj();
    }

    /**
     * Only not {@code null} if {@link #getType()} is {@link Type#UPDATE} and {@link ConfigFlag#COPY_GUILD_SCHEDULED_EVENTS_ON_UPDATE}
     * is enabled.
     * @return the old {@link GuildScheduledEvent}
     */
    public @Nullable GuildScheduledEvent getOldGuildScheduledEvent() {
        return scheduledEvent.getObj();
    }
}
