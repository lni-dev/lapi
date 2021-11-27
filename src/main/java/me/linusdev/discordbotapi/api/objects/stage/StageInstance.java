package me.linusdev.discordbotapi.api.objects.stage;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
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
     * @return {@link Data} for this {@link StageInstance}
     */
    @Override
    public Data getData() {
        Data data = new Data(6);

        data.add(ID_KEY, id);
        data.add(GUILD_ID_KEY, guildId);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(TOPIC_KEY, topic);
        data.add(PRIVACY_LEVEL_KEY, privacyLevel);
        data.add(DISCOVERABLE_DISABLED_KEY, discoverableDisabled);

        return data;
    }
}
