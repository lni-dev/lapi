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

package me.linusdev.lapi.api.communication.gateway.events.typing;

import me.linusdev.data.AbstractData;
import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypingStartEventFields implements Datable {

    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String USER_ID_KEY = "user_id";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String MEMBER_KEY = "member";

    private final @NotNull Snowflake channelId;
    private final @Nullable Snowflake guildId;
    private final @NotNull Snowflake userId;
    private final long timestamp;
    private final @Nullable Member member;

    public TypingStartEventFields(@NotNull Snowflake channelId, @Nullable Snowflake guildId, @NotNull Snowflake userId,
                                  long timestamp, @Nullable Member member) {
        this.channelId = channelId;
        this.guildId = guildId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.member = member;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable TypingStartEventFields fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        Snowflake channelId = data.getAndConvert(CHANNEL_ID_KEY, Snowflake::fromString);
        Snowflake guildId = data.getAndConvert(GUILD_ID_KEY, Snowflake::fromString);
        Snowflake userId = data.getAndConvert(USER_ID_KEY, Snowflake::fromString);
        Number timeInSeconds = (Number) data.get(TIMESTAMP_KEY);
        Member member = data.getAndConvertWithException(MEMBER_KEY, (SOData d) -> Member.fromData(lApi, d), null);

        if(channelId == null || userId == null || timeInSeconds == null) {
            InvalidDataException.throwException(data, null, TypingStartEventFields.class,
                    new Object[]{channelId, userId, timeInSeconds},
                    new String[]{CHANNEL_ID_KEY, USER_ID_KEY, TIMESTAMP_KEY});
            return null;
        }

        return new TypingStartEventFields(channelId, guildId, userId, timeInSeconds.longValue() * 1000L, member);
    }

    /**
     * 	id of the channel as {@link Snowflake}
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * 	id of the channel as {@link String}
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }

    /**
     * id of the guild as {@link Snowflake}
     */
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * id of the guild as {@link String}
     */
    public @Nullable String getGuildId() {
        return guildId == null ? null : guildId.asString();
    }

    /**
     * id of the user as {@link Snowflake}
     */
    public @NotNull Snowflake getUserIdAsSnowflake() {
        return userId;
    }

    /**
     * id of the user as {@link String}
     */
    public @NotNull String getUserId() {
        return userId.asString();
    }

    /**
     * 	unix time (in milliseconds) of when the user started typing
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * {@link ISO8601Timestamp} of {@link #getTimestamp()}
     */
    public ISO8601Timestamp getTimestampAsISO8601Timestamp() {
        return ISO8601Timestamp.of(timestamp);
    }

    /**
     * the member who started typing if this happened in a guild
     */
    public @Nullable Member getMember() {
        return member;
    }

    @Override
    public AbstractData<?, ?> getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(5);

        data.add(CHANNEL_ID_KEY, channelId);
        data.add(GUILD_ID_KEY, guildId);
        data.add(USER_ID_KEY, userId);
        data.add(TIMESTAMP_KEY, timestamp / 1000);
        data.add(MEMBER_KEY, member);

        return data;
    }
}
