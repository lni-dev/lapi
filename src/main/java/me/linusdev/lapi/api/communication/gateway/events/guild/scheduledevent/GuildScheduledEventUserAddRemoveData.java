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

package me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent;

import me.linusdev.data.AbstractData;
import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#guild-scheduled-event-user-add-guild-scheduled-event-user-add-event-fields" target="_top">Guild Scheduled Event User Add Event Fields</a>
 */
public class GuildScheduledEventUserAddRemoveData implements Datable {

    public static final String GUILD_SCHEDULED_EVENT_ID_KEY = "guild_scheduled_event_id";
    public static final String USER_ID_KEY = "user_id";
    public static final String GUILD_ID_KEY = "guild_id";

    private final @NotNull Snowflake scheduledEventId;
    private final @NotNull Snowflake userId;
    private final @NotNull Snowflake guildId;

    /**
     *
     * @param scheduledEventId id of the guild scheduled event
     * @param userId id of the user
     * @param guildId id of the guild
     */
    public GuildScheduledEventUserAddRemoveData(@NotNull Snowflake scheduledEventId, @NotNull Snowflake userId, @NotNull Snowflake guildId) {
        this.scheduledEventId = scheduledEventId;
        this.userId = userId;
        this.guildId = guildId;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable GuildScheduledEventUserAddRemoveData fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        Snowflake scheduledEventId = data.getAndConvert(GUILD_SCHEDULED_EVENT_ID_KEY, Snowflake::fromString);
        Snowflake userId = data.getAndConvert(USER_ID_KEY, Snowflake::fromString);
        Snowflake guildId = data.getAndConvert(GUILD_ID_KEY, Snowflake::fromString);

        if(scheduledEventId == null || userId == null || guildId == null) {
            InvalidDataException.throwException(data, null, GuildScheduledEventUserAddRemoveData.class,
                    new Object[]{scheduledEventId, userId, guildId},
                    new String[]{GUILD_SCHEDULED_EVENT_ID_KEY, USER_ID_KEY, GUILD_ID_KEY});
            return null; //will never be executed
        }

        return new GuildScheduledEventUserAddRemoveData(scheduledEventId, userId, guildId);
    }

    /**
     * id as {@link Snowflake} of the guild scheduled event
     */
    public @NotNull Snowflake getScheduledEventIdAsSnowflake() {
        return scheduledEventId;
    }

    /**
     * id as {@link String} of the guild scheduled event
     */
    public @NotNull String getScheduledEventId() {
        return scheduledEventId.asString();
    }

    /**
     * id as {@link Snowflake} of the user
     */
    public @NotNull Snowflake getUserIdAsSnowflake() {
        return userId;
    }

    /**
     * id as {@link String} of the user
     */
    public @NotNull String getUserId() {
        return userId.asString();
    }

    /**
     * id as {@link Snowflake} of the guild
     */
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * id as {@link String} of the guild
     */
    public @NotNull String getGuildId() {
        return guildId.asString();
    }

    @Override
    public AbstractData<?, ?> getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(GUILD_SCHEDULED_EVENT_ID_KEY, scheduledEventId);
        data.add(USER_ID_KEY, userId);
        data.add(GUILD_ID_KEY, guildId);

        return data;
    }
}
