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

package me.linusdev.lapi.api.communication.gateway.events.channel;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelUpdateEvent extends Event {

    private final @NotNull Update<Channel<?>, Channel<?>> update;

    public ChannelUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                              @NotNull Update<Channel<?>, Channel<?>> update) {
        super(lApi, payload, guildId);
        this.update = update;
    }

    public @NotNull Update<Channel<?>, Channel<?>> getUpdate() {
        return update;
    }

    /**
     * @return the updated {@link Channel}
     */
    public @NotNull Channel<?> getChannel() {
        return update.getObj();
    }

    /**
     *
     * @return copy of the {@link Channel} before it was updated or {@code null} if
     * {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_CHANNEL_ON_UPDATE_EVENT COPY_CHANNEL_ON_UPDATE_EVENT} is not enabled.
     */
    public @Nullable Channel<?> getOldChannel() {
        return update.getCopy();
    }
}
