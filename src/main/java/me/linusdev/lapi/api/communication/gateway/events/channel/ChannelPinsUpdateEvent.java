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
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sent when a message is pinned or unpinned in a text channel. This is not sent when a pinned message is deleted.
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-pins-update" target="_TOP">
 *     Channel Pins Update</a>
 */
public class ChannelPinsUpdateEvent extends Event {

    private final @NotNull Snowflake channelId;
    private final @Nullable ISO8601Timestamp lastPinTimestamp;

    public ChannelPinsUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId, @NotNull Snowflake channelId, @Nullable ISO8601Timestamp lastPinTimestamp) {
        super(lApi, payload, guildId);
        this.channelId = channelId;
        this.lastPinTimestamp = lastPinTimestamp;
    }

    /**
     * the id of the channel as {@link Snowflake}
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the id of the channel as {@link String}
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }

    /**
     * the time at which the most recent pinned message was pinned
     */
    public @Nullable ISO8601Timestamp getLastPinTimestamp() {
        return lastPinTimestamp;
    }
}
