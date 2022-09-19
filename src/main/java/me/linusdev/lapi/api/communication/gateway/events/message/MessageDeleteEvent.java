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

package me.linusdev.lapi.api.communication.gateway.events.message;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#message-delete" target="_TOP">Discord Documentation</a>
 */
public class MessageDeleteEvent extends Event {

    private final @NotNull Snowflake id;
    private final @NotNull Snowflake channelId;

    public MessageDeleteEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId, @NotNull Snowflake id, @NotNull Snowflake channelId) {
        super(lApi, payload, guildId);
        this.id = id;
        this.channelId = channelId;
    }

    /**
     * the id as {@link Snowflake} of the message
     */
    public @NotNull Snowflake getMessageIdAsSnowflake() {
        return id;
    }

    /**
     * the id as {@link String} of the message
     */
    public @NotNull String getMessageId() {
        return id.asString();
    }

    /**
     * the id as {@link Snowflake} of the channel the message is in.
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the id as {@link String} of the channel the message is in.
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }
}
