package me.linusdev.discordbotapi.api.objects.guild.scheduledevent;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.guild.GuildAbstract;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * <p>
 *     A representation of a scheduled event in a {@link GuildAbstract guild}.
 * </p>
 *
 * <p>
 *     {@link #getCreatorId() creator_id} will be {@code null} and {@link #getCreator() creator} will not be included for events created before October 25th, 2021, when the concept of {@link #getCreatorId() creator_id} was introduced and tracked.
 * </p>
 *
 * <p>
 *     See {@link EntityType field requirements by entity type} to understand the relationship between {@link #getEntityType() entity_type} and the following fields: {@link #getChannelId() channel_id}, {@link #getEntityMetadata() entity_metadata}, and {@link #getScheduledEndTime() scheduled_end_time}
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object" target="_top">Guild Scheduled Event Object</a>
 */
public class GuildScheduledEvent implements Datable, HasLApi, SnowflakeAble {

    public static final String ID_KEY = "id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String CREATOR_ID_KEY = "creator_id";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String SCHEDULED_START_TIME_KEY = "scheduled_start_time";
    public static final String SCHEDULED_END_TIME_KEY = "scheduled_end_time";
    public static final String PRIVACY_LEVEL_KEY = "privacy_level";
    public static final String STATUS_KEY = "status";
    public static final String ENTITY_TYPE_KEY = "entity_type";
    public static final String ENTITY_ID_KEY = "entity_id";
    public static final String ENTITY_METADATA_KEY = "entity_metadata";
    public static final String CREATOR_KEY = "creator";
    public static final String USER_COUNT_KEY = "user_count";

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @NotNull Snowflake guildId;
    private final @Nullable Snowflake channelId;
    private final @Nullable Snowflake creatorId;
    private final @NotNull String name;
    private final @Nullable String description;
    private final @NotNull ISO8601Timestamp scheduledStartTime;
    private final @Nullable ISO8601Timestamp scheduledEndTime;
    private final @NotNull PrivacyLevel privacyLevel;
    private final @NotNull Status status;
    private final @NotNull EntityType entityType;
    private final @Nullable Snowflake entityId;
    private final @Nullable EntityMetadata entityMetadata;
    private final @Nullable User creator;
    private final @Nullable Integer userCount;

    /**
     *
     * @param lApi {@link LApi}
     * @param id the id of the scheduled event
     * @param guildId the guild id which the scheduled event belongs to
     * @param channelId the channel id in which the scheduled event will be hosted, or null if {@link #getEntityType() scheduled entity type} is {@link EntityType#EXTERNAL}
     * @param creatorId the id of the user that created the scheduled event
     * @param name the name of the scheduled event (1-100 characters)
     * @param description the description of the scheduled event (1-1000 characters)
     * @param scheduledStartTime the time the scheduled event will start
     * @param scheduledEndTime the time the scheduled event will end, required if {@link #getEntityType() entity type} is {@link EntityType#EXTERNAL}
     * @param privacyLevel the privacy level of the scheduled event
     * @param status the status of the scheduled event
     * @param entityType the type of the scheduled event
     * @param entityId the id of an entity associated with a guild scheduled event
     * @param entityMetadata additional metadata for the guild scheduled event
     * @param creator the user that created the scheduled event
     * @param userCount the number of users subscribed to the scheduled event
     */
    public GuildScheduledEvent(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull Snowflake guildId, @Nullable Snowflake channelId, @Nullable Snowflake creatorId, @NotNull String name, @Nullable String description, @NotNull ISO8601Timestamp scheduledStartTime, @Nullable ISO8601Timestamp scheduledEndTime, @NotNull PrivacyLevel privacyLevel, @NotNull Status status, @NotNull EntityType entityType, @Nullable Snowflake entityId, @Nullable EntityMetadata entityMetadata, @Nullable User creator, @Nullable Integer userCount) {
        this.lApi = lApi;
        this.id = id;
        this.guildId = guildId;
        this.channelId = channelId;
        this.creatorId = creatorId;
        this.name = name;
        this.description = description;
        this.scheduledStartTime = scheduledStartTime;
        this.scheduledEndTime = scheduledEndTime;
        this.privacyLevel = privacyLevel;
        this.status = status;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityMetadata = entityMetadata;
        this.creator = creator;
        this.userCount = userCount;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data} with required fields
     * @return {@link GuildScheduledEvent}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #GUILD_ID_KEY}, {@link #NAME_KEY}, {@link #SCHEDULED_START_TIME_KEY}, {@link #PRIVACY_LEVEL_KEY}, {@link #TATUS_KEY} or {@link #ENTITY_TYPE_KEY} are null or missing
     */
    public static @Nullable GuildScheduledEvent fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        String channelId = (String) data.get(CHANNEL_ID_KEY);
        String creatorId = (String) data.get(CREATOR_ID_KEY);
        String name = (String) data.get(NAME_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        String scheduledStartTime = (String) data.get(SCHEDULED_START_TIME_KEY);
        String scheduledEndTime = (String) data.get(SCHEDULED_END_TIME_KEY);
        Number privacyLevel = (Number) data.get(PRIVACY_LEVEL_KEY);
        Number status = (Number) data.get(STATUS_KEY);
        Number entityType = (Number) data.get(ENTITY_TYPE_KEY);
        String entityId = (String) data.get(ENTITY_ID_KEY);
        Data entityMetadata = (Data) data.get(ENTITY_METADATA_KEY);
        Data creator = (Data) data.get(CREATOR_KEY);
        Number userCount = (Number) data.get(USER_COUNT_KEY);

        if(id == null || guildId == null || name == null || scheduledStartTime == null ||
                privacyLevel == null || status == null || entityType == null){
            InvalidDataException.throwException(data, null, GuildScheduledEvent.class,
                    new Object[]{id, guildId, name, scheduledStartTime, privacyLevel, status, entityType},
                    new String[]{ID_KEY, GUILD_ID_KEY, NAME_KEY, SCHEDULED_START_TIME_KEY, PRIVACY_LEVEL_KEY, STATUS_KEY, ENTITY_TYPE_KEY});
            return null;
        }

        return new GuildScheduledEvent(lApi, Snowflake.fromString(id), Snowflake.fromString(guildId), Snowflake.fromString(channelId), Snowflake.fromString(creatorId),
                name, description, ISO8601Timestamp.fromString(scheduledStartTime), ISO8601Timestamp.fromString(scheduledEndTime), PrivacyLevel.fromValue(privacyLevel.intValue()),
                Status.fromValue(status.intValue()), EntityType.fromValue(entityType.intValue()), Snowflake.fromString(entityId), EntityMetadata.fromData(entityMetadata),
                creator == null ? null : User.fromData(lApi, creator), userCount == null ? null : userCount.intValue());
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the guild id as {@link Snowflake} which the scheduled event belongs to
     */
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * the guild id as {@link String} which the scheduled event belongs to
     */
    public @NotNull String getGuildId() {
        return guildId.asString();
    }

    /**
     * the channel id as {@link Snowflake} in which the scheduled event will be hosted, or null if {@link #getEntityType() scheduled entity type} is {@link EntityType#EXTERNAL}
     */
    public @Nullable Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the channel id as {@link String} in which the scheduled event will be hosted, or null if {@link #getEntityType() scheduled entity type} is {@link EntityType#EXTERNAL}
     */
    public @Nullable String getChannelId() {
        if(channelId == null) return null;
        return channelId.asString();
    }

    /**
     * the id as {@link Snowflake} of the user that created the scheduled event
     */
    public @Nullable Snowflake getCreatorIdAsSnowflake() {
        return creatorId;
    }

    /**
     * the id as {@link String} of the user that created the scheduled event
     */
    public @Nullable String getCreatorId() {
        if(creatorId == null) return null;
        return creatorId.asString();
    }

    /**
     * the name of the scheduled event (1-100 characters)
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * the description of the scheduled event (1-1000 characters)
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * the time the scheduled event will start
     */
    public @NotNull ISO8601Timestamp getScheduledStartTime() {
        return scheduledStartTime;
    }

    /**
     * the time the scheduled event will end, required if {@link #getEntityType() entity type} is {@link EntityType#EXTERNAL}
     */
    public @Nullable ISO8601Timestamp getScheduledEndTime() {
        return scheduledEndTime;
    }

    /**
     * the privacy level of the scheduled event
     */
    public @NotNull PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    /**
     * the status of the scheduled event
     */
    public @NotNull Status getStatus() {
        return status;
    }

    /**
     * the type of the scheduled event
     * @see EntityType
     */
    public @NotNull EntityType getEntityType() {
        return entityType;
    }

    /**
     * the id as {@link Snowflake} of an entity associated with a guild scheduled event
     */
    public @Nullable Snowflake getEntityIdAsSnowflake() {
        return entityId;
    }

    /**
     * the id as {@link String} of an entity associated with a guild scheduled event
     */
    public @Nullable String getEntityId() {
        if(entityId == null) return null;
        return entityId.asString();
    }

    /**
     * additional metadata for the guild scheduled event
     */
    public @Nullable EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    /**
     * the user that created the scheduled event
     */
    public @Nullable User getCreator() {
        return creator;
    }

    /**
     * the number of users subscribed to the scheduled event
     */
    public @Nullable Integer getUserCount() {
        return userCount;
    }

    /**
     *
     * @return {@link Data} for this {@link GuildScheduledEvent}
     */
    @Override
    public @NotNull Data getData() {
        Data data = new Data(0);

        data.add(ID_KEY, id);
        data.add(GUILD_ID_KEY, guildId);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(CREATOR_ID_KEY, creatorId);
        data.add(NAME_KEY, name);
        if(description != null) data.add(DESCRIPTION_KEY, description);
        data.add(SCHEDULED_START_TIME_KEY, scheduledStartTime);
        data.add(SCHEDULED_END_TIME_KEY, scheduledEndTime);
        data.add(PRIVACY_LEVEL_KEY, privacyLevel);
        data.add(STATUS_KEY, status);
        data.add(ENTITY_TYPE_KEY, entityType);
        data.add(ENTITY_ID_KEY, entityId);
        data.add(ENTITY_METADATA_KEY, entityMetadata);
        if(creator != null) data.add(CREATOR_KEY, creator);
        if(userCount != null) data.add(USER_COUNT_KEY, userCount);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
