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

package me.linusdev.lapi.api.objects.stage;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO add docs from discord
 *
 * @see <a href="https://discord.com/developers/docs/resources/stage-instance#stage-instance-resource" target="_top">Stage Instance Resource</a>
 */
public class StageInstance implements Datable, SnowflakeAble, CopyAndUpdatable<StageInstance> {

    public static final String ID_KEY = "id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String TOPIC_KEY = "topic";
    public static final String PRIVACY_LEVEL_KEY = "privacy_level";
    public static final String DISCOVERABLE_DISABLED_KEY = "discoverable_disabled";
    public static final String GUILD_SCHEDULED_EVENT_ID_KEY = "guild_scheduled_event_id";

    private final @NotNull Snowflake id;
    private final @NotNull Snowflake guildId;
    private final @NotNull Snowflake channelId;
    private @NotNull String topic;
    private @NotNull PrivacyLevel privacyLevel;
    private boolean discoverableDisabled;
    private @Nullable Snowflake guildScheduledEventId;

    /**
     * @param id                    The id of this Stage instance
     * @param guildId               The guild id of the associated Stage channel
     * @param channelId             The id of the associated Stage channel
     * @param topic                 The topic of the Stage instance (1-120 characters)
     * @param privacyLevel          The privacy level of the Stage instance
     * @param discoverableDisabled  Whether or not Stage Discovery is disabled
     * @param guildScheduledEventId The id of the scheduled event for this Stage instance
     */
    public StageInstance(@NotNull Snowflake id, @NotNull Snowflake guildId, @NotNull Snowflake channelId, @NotNull String topic, @NotNull PrivacyLevel privacyLevel, boolean discoverableDisabled, @Nullable Snowflake guildScheduledEventId) {
        this.id = id;
        this.guildId = guildId;
        this.channelId = channelId;
        this.topic = topic;
        this.privacyLevel = privacyLevel;
        this.discoverableDisabled = discoverableDisabled;
        this.guildScheduledEventId = guildScheduledEventId;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable StageInstance fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        Snowflake id = data.getAndConvert(ID_KEY, Snowflake::fromString);
        Snowflake guildId = data.getAndConvert(GUILD_ID_KEY, Snowflake::fromString);
        Snowflake channelId = data.getAndConvert(CHANNEL_ID_KEY, Snowflake::fromString);
        String topic = (String) data.get(TOPIC_KEY);
        Number privacyLevel = (Number) data.get(PRIVACY_LEVEL_KEY);
        Boolean discoverableDisabled = (Boolean) data.get(DISCOVERABLE_DISABLED_KEY);
        Snowflake guildScheduledEventId = data.getAndConvert(GUILD_SCHEDULED_EVENT_ID_KEY, Snowflake::fromString);

        if(id == null || guildId == null || channelId == null || topic == null || privacyLevel == null || discoverableDisabled == null) {
            InvalidDataException.throwException(data, null, StageInstance.class,
                    new Object[]{id, guildId, channelId, topic, privacyLevel, discoverableDisabled},
                    new String[]{ID_KEY, GUILD_ID_KEY, CHANNEL_ID_KEY, TOPIC_KEY, PRIVACY_LEVEL_KEY, DISCOVERABLE_DISABLED_KEY});
            return null; //appeasing the null-checks
        }

        return new StageInstance(id, guildId, channelId, topic, PrivacyLevel.fromValue(privacyLevel.intValue()),
                discoverableDisabled, guildScheduledEventId);
    }

    /**
     * The id as {@link Snowflake} of this Stage instance
     */
    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * The guild id of the associated Stage channel
     */
    public @NotNull String getGuildId() {
        return guildId.asString();
    }

    /**
     * The id of the associated Stage channel
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }

    /**
     * The guild id {@link Snowflake} of the associated Stage channel
     */
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * The id as {@link Snowflake} of the associated Stage channel
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * The topic of the Stage instance (1-120 characters)
     */
    public @NotNull String getTopic() {
        return topic;
    }

    /**
     * The privacy level of the Stage instance
     */
    public @NotNull PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    /**
     * Whether or not Stage Discovery is disabled
     */
    public boolean isDiscoverableDisabled() {
        return discoverableDisabled;
    }

    /**
     * The id as {@link Snowflake} of the scheduled event for this Stage instance
     */
    public @Nullable Snowflake getGuildScheduledEventIdAsSnowflake() {
        return guildScheduledEventId;
    }

    /**
     * The id as {@link String} of the scheduled event for this Stage instance
     */
    public @Nullable String getGuildScheduledEventId() {
        if (guildScheduledEventId == null) return null;
        return guildScheduledEventId.asString();
    }

    /**
     * @return {@link SOData} for this {@link StageInstance}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(7);

        data.add(ID_KEY, id);
        data.add(GUILD_ID_KEY, guildId);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(TOPIC_KEY, topic);
        data.add(PRIVACY_LEVEL_KEY, privacyLevel);
        data.add(DISCOVERABLE_DISABLED_KEY, discoverableDisabled);
        data.add(GUILD_SCHEDULED_EVENT_ID_KEY, guildScheduledEventId);

        return data;
    }

    @Override
    public @NotNull StageInstance copy() {
        return new StageInstance(
                Copyable.copy(id),
                Copyable.copy(guildId),
                Copyable.copy(channelId),
                Copyable.copy(topic),
                privacyLevel,
                discoverableDisabled,
                Copyable.copy(guildScheduledEventId));
    }

    @Override
    public void updateSelfByData(@NotNull SOData data) throws InvalidDataException {
        data.processIfContained(TOPIC_KEY, (String str) -> this.topic = str);
        data.processIfContained(PRIVACY_LEVEL_KEY, (Number num) -> {
            if (num != null) this.privacyLevel = PrivacyLevel.fromValue(num.intValue());
        });
        data.processIfContained(DISCOVERABLE_DISABLED_KEY, (Boolean bool) -> {
            if (bool != null) this.discoverableDisabled = bool;
        });
        data.processIfContained(GUILD_SCHEDULED_EVENT_ID_KEY, (String str) -> this.guildScheduledEventId = Snowflake.fromString(str));
    }
}
