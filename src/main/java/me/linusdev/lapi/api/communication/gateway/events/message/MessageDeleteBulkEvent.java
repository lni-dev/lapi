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

import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#message-delete-bulk" target="_TOP">Discord Documentation</a>
 */
public class MessageDeleteBulkEvent extends Event {

    public static final String IDS_KEY = "ids";

    private final @NotNull List<String> messageIds;
    private final @NotNull Snowflake channelId;

    public MessageDeleteBulkEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                  @Nullable Snowflake guildId, @NotNull List<String> messageIds,
                                  @NotNull Snowflake channelId) {
        super(lApi, payload, guildId);
        this.messageIds = messageIds;
        this.channelId = channelId;
    }

    /**
     * List of all deleted messages id's as {@link String}.<br>
     * Every id can be converted to a {@link Snowflake} with the function {@link Snowflake#fromString(String)} if necessary
     */
    public @NotNull List<String> getMessageIds() {
        return messageIds;
    }

    /**
     * the id of the channel the messages were deleted in.
     */
    public @NotNull Snowflake getChannelId() {
        return channelId;
    }
}
