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
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sent when a user adds or removes a reaction
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#message-reaction-add" target="_TOP">
 *     Discord Documentation (Reaction Add)</a>
 * @see <a href="https://discord.com/developers/docs/topics/gateway#message-reaction-remove" target="_TOP">
 *     Discord Documentation (Reaction Remove)</a>
 */
public class MessageReactionEvent extends Event {

    private final @NotNull MessageReactionEventType type;
    private final @NotNull MessageReactionEventFields fields;

    public MessageReactionEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                @NotNull MessageReactionEventType type, @NotNull MessageReactionEventFields fields) {
        super(lApi, payload, fields.getGuildId());
        this.type = type;
        this.fields = fields;
    }

    /**
     * Whether this reaction was {@link MessageReactionEventType#ADD added} or
     * {@link MessageReactionEventType#REMOVE removed}.
     *
     * <br><br>
     *
     * If this reaction was {@link MessageReactionEventType#REMOVE removed}, {@link #getMember()}
     * will always be {@code null}
     */
    public @NotNull MessageReactionEventType getType() {
        return type;
    }

    /**
     * the id as {@link Snowflake} of the user
     */
    public @NotNull Snowflake getUserIdAsSnowflake() {
        return fields.getUserId();
    }

    /**
     * the id as {@link String} of the user
     */
    public @NotNull String getUserId() {
        return fields.getUserId().asString();
    }

    /**
     * the id as {@link Snowflake} of the channel
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return fields.getChannelId();
    }

    /**
     * the id as {@link String} of the channel
     */
    public @NotNull String getChannelId() {
        return fields.getChannelId().asString();
    }

    /**
     * the id as {@link Snowflake} of the message
     */
    public @NotNull Snowflake getMessageIdAsSnowflake() {
        return fields.getMessageId();
    }

    /**
     * the id as {@link String} of the message
     */
    public @NotNull String getMessageId() {
        return fields.getMessageId().asString();
    }

    /**
     * the member who reacted if this happened in a guild.
     * <br><br>
     * If this reaction was {@link MessageReactionEventType#REMOVE removed}, {@link #getMember()}
     * will always be {@code null}.
     */
    public @Nullable Member getMember() {
        return fields.getMember();
    }

    /**
     * the emoji used to react
     */
    public @NotNull EmojiObject getEmoji() {
        return fields.getEmoji();
    }
}
