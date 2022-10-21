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

package me.linusdev.lapi.api.communication.gateway.events.channel;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelDeleteEvent extends Event {

    private final @NotNull Channel channel;
    private final @Nullable Channel cachedChannel;

    public ChannelDeleteEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                              @Nullable Channel cachedChannel, @NotNull Channel channel) {
        super(lApi, payload, guildId);
        this.channel = channel;
        this.cachedChannel = cachedChannel;
    }

    /**
     *
     * @return the deleted {@link Channel}
     */
    public @NotNull Channel getChannel() {
        return channel;
    }

    /**
     *
     * @return the cached version of the channel that was deleted (this was already deleted from the cache) or {@code null} if cache is disabled.
     */
    public @Nullable Channel getCachedChannel() {
        return cachedChannel;
    }
}
