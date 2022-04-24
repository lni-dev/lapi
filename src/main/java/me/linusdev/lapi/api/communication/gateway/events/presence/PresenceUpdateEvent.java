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

package me.linusdev.lapi.api.communication.gateway.events.presence;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.presence.PresenceUpdate;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PresenceUpdateEvent extends Event {

    private final @NotNull Update<PresenceUpdate, PresenceUpdate> update;

    public PresenceUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                               @NotNull Update<PresenceUpdate, PresenceUpdate> update) {
        super(lApi, payload, update.getObj().getGuildId());

        this.update = update;
    }

    /**
     * @return the updated presence
     */
    public @NotNull PresenceUpdate getPresence() {
        return update.getObj();
    }

    /**
     * Will always be {@code null} if {@link ConfigFlag#CACHE_PRESENCES CACHE_PRESENCES} is not enabled.
     * @return the presence before it was updated or {@code null}
     */
    public @Nullable PresenceUpdate getOldPresence() {
        return update.getCopy();
    }

    /**
     *
     * @return {@code true} if no presence for given user in given guild was cached before this event.
     * If {@link ConfigFlag#CACHE_PRESENCES CACHE_PRESENCES} is not enabled, this will always return {@code false}
     */
    public boolean isNew() {
        return update.isNew();
    }
}
