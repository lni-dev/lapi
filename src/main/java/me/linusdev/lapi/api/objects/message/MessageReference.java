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

package me.linusdev.lapi.api.objects.message;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     {@link #getChannelId() channel_id} is optional when creating a reply, but will always be present when receiving an event/response that includes this data model.
 * </p>
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-reference-object-message-reference-structure" target="_top">Message Reference Structure</a>
 */
public class MessageReference implements Datable {

    public static final String MESSAGE_ID_KEY = "message_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String FALL_IF_NOT_EXISTS_KEY = "fail_if_not_exists";

    private final @Nullable Snowflake messageId;
    private final @Nullable Snowflake channelId;
    private final @Nullable Snowflake guildId;
    private final @Nullable Boolean fallIfNotExists;

    /**
     *
     * @param messageId id of the originating message
     * @param channelId id of the originating message's channel
     * @param guildId id of the originating message's guild
     * @param fallIfNotExists when sending, whether to error if the referenced message doesn't exist instead of sending as a normal (non-reply) message, default true
     */
    public MessageReference(@Nullable Snowflake messageId, @Nullable Snowflake channelId, @Nullable Snowflake guildId, @Nullable Boolean fallIfNotExists) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.guildId = guildId;
        this.fallIfNotExists = fallIfNotExists;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link MessageReference} or {@code null} if data was {@code null}
     */
    public static @Nullable MessageReference fromData(@Nullable Data data){
        if(data == null) return null;

        String messageId = (String) data.get(MESSAGE_ID_KEY);
        String channelId = (String) data.get(CHANNEL_ID_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        Boolean fallIfNotExists = (Boolean) data.get(FALL_IF_NOT_EXISTS_KEY);

        return new MessageReference(Snowflake.fromString(messageId), Snowflake.fromString(channelId), Snowflake.fromString(guildId), fallIfNotExists);
    }

    /**
     * id as {@link Snowflake} of the originating message
     */
    public @Nullable Snowflake getMessageIdAsSnowflake() {
        return messageId;
    }

    /**
     * id as {@link String} of the originating message
     */
    public String getMessageId() {
        return messageId == null ? null : messageId.asString();
    }

    /**
     * id as {@link Snowflake} of the originating message's channel
     */
    public @Nullable Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * id as {@link String} of the originating message's channel
     */
    public String getChannelId() {
        return channelId == null ? null : channelId.asString();
    }

    /**
     * id as {@link Snowflake} of the originating message's guild
     */
    public Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * id as {@link String} of the originating message's guild
     */
    public String getGuildId(){
        return guildId == null ? null : guildId.asString();
    }

    /**
     * when sending, whether to error if the referenced message doesn't exist instead of sending as a normal (non-reply) message, default {@code true}
     * @see #doesFallIfNotExists()
     */
    public @Nullable Boolean getFallIfNotExists(){
        return fallIfNotExists;
    }

    /**
     * when sending, whether to error if the referenced message doesn't exist instead of sending as a normal (non-reply) message, default {@code true}
     *
     * @return {@code true} if {@link #getFallIfNotExists()} == {@code null}, {@link #getFallIfNotExists()} otherwise
     */
    public boolean doesFallIfNotExists(){
        return (fallIfNotExists == null) || fallIfNotExists;
    }

    /**
     *
     * @return {@link Data} for this {@link MessageReference}
     */
    @Override
    public Data getData() {
        Data data = new Data(4);

        if(messageId != null) data.add(MESSAGE_ID_KEY, messageId);
        if(channelId != null) data.add(CHANNEL_ID_KEY, channelId);
        if(guildId != null) data.add(GUILD_ID_KEY, guildId);
        if(fallIfNotExists != null) data.add(FALL_IF_NOT_EXISTS_KEY, fallIfNotExists);

        return data;
    }
}
