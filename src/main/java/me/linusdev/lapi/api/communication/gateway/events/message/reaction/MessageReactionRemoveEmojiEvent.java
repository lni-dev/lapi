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

package me.linusdev.lapi.api.communication.gateway.events.message.reaction;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sent when a bot removes all instances of a given emoji from the reactions of a message.
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#message-reaction-remove-emoji" target="_TOP">
 *     Discord Documentation
 *     </a>
 */
public class MessageReactionRemoveEmojiEvent extends Event {

    private final @NotNull Snowflake channelId;
    private final @NotNull Snowflake messageId;
    private final @NotNull EmojiObject emoji;

    public MessageReactionRemoveEmojiEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId, @NotNull Snowflake channelId, @NotNull Snowflake messageId, @NotNull EmojiObject emoji) {
        super(lApi, payload, guildId);
        this.channelId = channelId;
        this.messageId = messageId;
        this.emoji = emoji;
    }

    /**
     * the id as {@link Snowflake} of the channel
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the id as {@link String} of the channel
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }

    /**
     * the id as {@link Snowflake} of the message
     */
    public @NotNull Snowflake getMessageIdAsSnowflake() {
        return messageId;
    }

    /**
     * the id as {@link String} of the message
     */
    public @NotNull String getMessageId() {
        return messageId.asString();
    }

    /**
     * the emoji that was removed
     */
    public @NotNull EmojiObject getEmoji() {
        return emoji;
    }
}
