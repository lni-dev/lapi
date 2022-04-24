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
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;

/**
 *
 * TODO add docs from discord
 * @see <a href="https://discord.com/developers/docs/resources/stage-instance#stage-instance-resource" target="_top">Stage Instance Resource</a>
 */
public class StageInstance implements Datable, SnowflakeAble {

    public static final String ID_KEY = "id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String TOPIC_KEY = "topic";
    public static final String PRIVACY_LEVEL_KEY = "privacy_level";
    public static final String DISCOVERABLE_DISABLED_KEY = "discoverable_disabled";

    private final @NotNull Snowflake id;
    private final @NotNull Snowflake guildId;
    private final @NotNull Snowflake channelId;
    private final @NotNull String topic;
    private final @NotNull PrivacyLevel privacyLevel;
    private final boolean discoverableDisabled;

    /**
     *
     * @param id The id of this Stage instance
     * @param guildId The guild id of the associated Stage channel
     * @param channelId The id of the associated Stage channel
     * @param topic The topic of the Stage instance (1-120 characters)
     * @param privacyLevel The privacy level of the Stage instance
     * @param discoverableDisabled Whether or not Stage Discovery is disabled
     */
    public StageInstance(@NotNull Snowflake id, @NotNull Snowflake guildId, @NotNull Snowflake channelId, @NotNull String topic, @NotNull PrivacyLevel privacyLevel, boolean discoverableDisabled) {
        this.id = id;
        this.guildId = guildId;
        this.channelId = channelId;
        this.topic = topic;
        this.privacyLevel = privacyLevel;
        this.discoverableDisabled = discoverableDisabled;
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
    public @NotNull Snowflake getGuildId() {
        return guildId;
    }

    /**
     * The id of the associated Stage channel
     */
    public @NotNull Snowflake getChannelId() {
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
     *
     * @return {@link SOData} for this {@link StageInstance}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add(ID_KEY, id);
        data.add(GUILD_ID_KEY, guildId);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(TOPIC_KEY, topic);
        data.add(PRIVACY_LEVEL_KEY, privacyLevel);
        data.add(DISCOVERABLE_DISABLED_KEY, discoverableDisabled);

        return data;
    }
}
