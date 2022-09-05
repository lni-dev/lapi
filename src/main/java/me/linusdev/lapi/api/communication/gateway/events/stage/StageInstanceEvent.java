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

package me.linusdev.lapi.api.communication.gateway.events.stage;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.stage.StageInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StageInstanceEvent extends Event {

    /**
     * Type of this event
     */
    public enum Type {
        /**
         * This is a created event.
         */
        CREATE,

        /**
         * This is an update event.
         */
        UPDATE,

        /**
         * This is a deleted event.
         */
        DELETE,
    }

    private final @NotNull Update<StageInstance, StageInstance> update;
    private final @NotNull Type type;

    public StageInstanceEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                              @NotNull Update<StageInstance, StageInstance> update, @NotNull Type type) {
        super(lApi, payload, update.getObj().getGuildIdAsSnowflake());

        this.update = update;
        this.type = type;
    }


    /**
     * The {@link #getType() updated, created or deleted} {@link StageInstance}
     * @return {@link StageInstance}
     * @see #getType()
     */
    public @NotNull StageInstance getStageInstance() {
        return update.getObj();
    }

    /**
     * Only available if {@link #getType()} is {@link Type#UPDATE UPDATE} and
     * {@link ConfigFlag#CACHE_STAGE_INSTANCES CACHE_STAGE_INSTANCES} is enabled.
     * @return a copy of the {@link StageInstance} before it was updated.
     */
    public @Nullable StageInstance getOldStageInstance() {
        return update.getCopy();
    }

    /**
     * Whether this event is a {@link Type#CREATE}, {@link Type#UPDATE} or {@link Type#DELETE} event.
     */
    public @NotNull Type getType() {
        return type;
    }
}
