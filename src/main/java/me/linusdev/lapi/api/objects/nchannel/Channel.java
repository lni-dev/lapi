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

package me.linusdev.lapi.api.objects.nchannel;

import me.linusdev.data.Datable;
import me.linusdev.data.OptionalValue;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManager;
import me.linusdev.lapi.api.objects.interaction.ResolvedData;
import me.linusdev.lapi.api.objects.nchannel.thread.AutoArchiveDuration;
import me.linusdev.lapi.api.objects.nchannel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.nchannel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.nchannel.thread.ThreadChannel;
import me.linusdev.lapi.api.objects.nchannel.forum.DefaultReaction;
import me.linusdev.lapi.api.objects.nchannel.forum.ForumTag;
import me.linusdev.lapi.api.objects.nchannel.forum.SortOrderType;
import me.linusdev.lapi.api.objects.enums.VideoQuality;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrite;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a guild or DM channel within Discord.
 * @see <a href="https://discord.com/developers/docs/resources/channel">Discord Documentation</a>
 * @see PartialChannel
 * @see ThreadChannel
 */
@SuppressWarnings({"UnnecessaryModifier", "unused"})
public interface Channel extends SnowflakeAble, Datable, HasLApi {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                    Limits                                                 *
     *                                                                                                           *
     *                                                                                                           *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final int CHANNEL_NAME_MIN_CHARS = 1;
    public static final int CHANNEL_NAME_MAX_CHARS = 100;

    public static final int CHANNEL_TOPIC_MIN_CHARS = 0;
    public static final int CHANNEL_TOPIC_MAX_CHARS = 1024;

    public static final int FORUM_CHANNEL_TOPIC_MIN_CHARS = CHANNEL_TOPIC_MIN_CHARS;
    public static final int FORUM_CHANNEL_TOPIC_MAX_CHARS = 4096;

    public static final int MAX_CHANNELS_IN_CATEGORY = 50;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                    Keys                                                   *
     *                                                                                                           *
     *                                                                                                           *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final String ID_KEY = "id";
    public static final String TYPE_KEY = "type";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String POSITION_KEY = "position";
    public static final String PERMISSION_OVERWRITES_KEY = "permission_overwrites";
    public static final String NAME_KEY = "name";
    public static final String TOPIC_KEY = "topic";
    public static final String NSFW_KEY = "nsfw";
    public static final String LAST_MESSAGE_ID_KEY = "last_message_id";
    public static final String BITRATE_KEY = "bitrate";
    public static final String USER_LIMIT_KEY = "user_limit";
    public static final String RATE_LIMIT_PER_USER_KEY = "rate_limit_per_user";
    public static final String RECIPIENTS_KEY = "recipients";
    public static final String ICON_KEY = "icon";
    public static final String OWNER_ID_KEY = "owner_id";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String PARENT_ID_KEY = "parent_id";
    public static final String LAST_PIN_TIMESTAMP_KEY = "last_pin_timestamp";
    public static final String RTC_REGION_KEY = "rtc_region";
    public static final String VIDEO_QUALITY_MODE_KEY = "video_quality_mode";
    public static final String MESSAGE_COUNT_KEY = "message_count";
    public static final String MEMBER_COUNT_KEY = "member_count";
    public static final String THREAD_METADATA_KEY = "thread_metadata";
    public static final String MEMBER_KEY = "member";
    public static final String DEFAULT_AUTO_ARCHIVE_DURATION_KEY = "default_auto_archive_duration";
    public static final String PERMISSIONS_KEY = "permissions";
    public static final String FLAGS_KEY = "flags";
    public static final String TOTAL_MESSAGE_SENT_KEY = "total_message_sent";
    public static final String AVAILABLE_TAGS_KEY = "available_tags";
    public static final String APPLIED_TAGS_KEY = "applied_tags";
    public static final String DEFAULT_REACTION_EMOJI_KEY = "default_reaction_emoji";
    public static final String DEFAULT_THREAD_RATE_LIMIT_PER_USER_KEY = "default_thread_rate_limit_per_user";
    public static final String DEFAULT_SORT_ORDER_KEY = "default_sort_order";




    /**
     * The id of this channel.
     */
    @Override
    @NotNull String getId();

    /**
     * the id as {@link Snowflake} of this channel.
     */
    @Override
    default @NotNull Snowflake getIdAsSnowflake() {
        return Snowflake.fromString(getId());
    }

    /**
     * The {@link ChannelType type of channel}.
     */
    @NotNull ChannelType getType();

    /**
     * The id of the guild (may be missing for some channel objects received over gateway guild dispatches).
     */
    @Nullable String getGuild();

    /**
     * The id as {@link Snowflake} of the guild (may be missing for some channel objects received over gateway guild dispatches).
     */
    default @Nullable Snowflake getGuildIdAsSnowflake() {
        return Snowflake.fromString(getGuild());
    }

    /**
     * Sorting position of the channel.
     */
    @Nullable Integer getPosition();

    /**
     * Explicit permission overwrites for members and roles.
     */
    @Nullable List<PermissionOverwrite> getPermissionOverwrites();

    /**
     * The name of the channel ({@value #CHANNEL_NAME_MIN_CHARS}-{@value CHANNEL_NAME_MAX_CHARS} characters).
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<String> getName();

    /**
     * The channel topic ({@value #FORUM_CHANNEL_TOPIC_MIN_CHARS}-{@value FORUM_CHANNEL_TOPIC_MAX_CHARS} characters
     * for {@link ChannelType#GUILD_FORUM GUILD_FORUM} channels,
     * {@value #CHANNEL_TOPIC_MIN_CHARS}-{@value CHANNEL_TOPIC_MAX_CHARS} characters for all others).
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<String> getTopic();

    /**
     * Whether the channel is nsfw.
     */
    @Nullable Boolean isNSFW();

    /**
     * The id of the last message sent in this channel (or thread for {@link ChannelType#GUILD_FORUM GUILD_FORUM} channels)
     * (may not point to an existing or valid message or thread).
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<String> getLastMessageId();

    /**
     * The id as {@link Snowflake} of the last message sent in this channel (or thread for {@link ChannelType#GUILD_FORUM GUILD_FORUM} channels)
     * (may not point to an existing or valid message or thread).
     */
    default @Nullable Snowflake getLastMessageIdAsSnowflake() {
        return Snowflake.fromString(getLastMessageId().get());
    }

    /**
     * The bitrate (in bits) of the voice channel.
     */
    @Nullable Integer getBitrate();

    /**
     * The user limit of the voice channel.
     */
    @Nullable Integer getUserLimit();

    /**
     * Amount of seconds a user has to wait before sending another message (0-21600).
     * Bots, as well as users with the permission {@link Permission#MANAGE_MESSAGES manage_messages}
     * or {@link Permission#MANAGE_CHANNELS manage_channel}, are unaffected.
     * <p>
     *     This also applies to thread creation. Users can send one message and create one thread during each interval.
     * </p>
     */
    @Nullable Integer getRateLimitPerUser();

    /**
     * The recipients of the DM.
     */
    @Nullable List<User> getRecipients();

    /**
     * Icon hash of the group DM.
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<String> getIcon();

    /**
     * Id of the creator of the group DM or thread.
     */
    @Nullable String getOwnerId();

    /**
     * Id as {@link Snowflake} of the creator of the group DM or thread.
     */
    default @Nullable Snowflake getOwnerIdAsSnowflake() {
        return Snowflake.fromString(getOwnerId());
    }

    /**
     * Application id of the group DM creator if it is bot-created.
     */
    @Nullable String getApplicationId();

    /**
     * Application id as {@link Snowflake} of the group DM creator if it is bot-created.
     */
    default @Nullable Snowflake getApplicationIdAsSnowflake() {
        return Snowflake.fromString(getApplicationId());
    }

    /**
     * For guild channels: id of the parent category for a channel (each parent category can contain up to
     * {@value #MAX_CHANNELS_IN_CATEGORY} channels).<br>
     * For threads: id of the text channel this thread was created.
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<String> getParentId();

    /**
     * See Documentation of {@link #getParentId()}.
     */
    default @Nullable Snowflake getParentIdAsSnowflake() {
        return Snowflake.fromString(getParentId().get());
    }

    /**
     * When the last pinned message was pinned. This may be {@code null} in events such as
     * {@link GatewayEvent#GUILD_CREATE GUILD_CREATE} when a message is not pinned.
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<ISO8601Timestamp> getLastPinTimestamp();

    /**
     * {@link VoiceRegion voice region} id for the voice channel, automatic when set to {@code null}.
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<String> getRTCRegion();

    /**
     * Get the {@link VoiceRegion} corresponding the id given by {@link #getRTCRegion()} from the cache.
     */
    default @Nullable VoiceRegion getRTCRegionAsVoiceRegionFromCache() {
        if(getRTCRegion().isNull()) return null;
        return VoiceRegionManager.getVoiceRegionById(getLApi(), getRTCRegion().get());
    }

    /**
     * The camera {@link VideoQuality video quality mode} of the voice channel, {@link VideoQuality#AUTO 1} when not present.
     */
    @Nullable VideoQuality getVideoQualityMode();

    /**
     * Number of messages (not including the initial message or deleted messages) in a thread.
     * <p>
     *     For threads created before July 1, 2022, the message count is inaccurate when it's greater than 50.
     * </p>
     */
    @Nullable Integer getMessageCount();

    /**
     * an approximate count of users in a thread, stops counting at 50.
     */
    @Nullable Integer getMemberCount();

    /**
     * thread-specific fields not needed by other channels.
     */
    @Nullable ThreadMetadata getThreadMetadata();

    /**
     * Thread member object for the current user, if they have joined the thread, only included on certain API endpoints.
     */
    @Nullable ThreadMember getMember();

    /**
     * Default duration, copied onto newly created threads, in minutes, threads will stop showing in the channel list
     * after the specified period of inactivity. Can be set to: {@link AutoArchiveDuration see here}.
     */
    @Nullable Integer getDefaultAutoArchiveDuration();

    /**
     * Computed permissions for the invoking user in the channel, including overwrites, only included when part of
     * the {@link ResolvedData resolved data} received on a slash command interaction.
     */
    @Nullable Permissions getPermissions();

    /**
     * {@link ChannelFlag Channel flags} combined as a bitfield.
     */
    @Nullable Integer getFlags();

    /**
     * {@link ChannelFlag Channel flags} as {@link List}.
     * @see #getFlags()
     */
    default @NotNull List<ChannelFlag> getFlagsAsList() {
        return ChannelFlag.fromBits(getFlags());
    }

    /**
     * Number of messages ever sent in a thread, it's similar to {@link #getMessageCount() message_count} on message creation,
     * but will not decrement the number when a message is deleted.
     */
    @Nullable Integer totalMessageSent();

    /**
     * The set of {@link ForumTag tags} that can be used in a {@link ChannelType#GUILD_FORUM GUILD_FORUM} channel.
     */
    @Nullable List<ForumTag> getAvailableTags();

    /**
     * The IDs of the set of tags that have been applied to a thread in a {@link ChannelType#GUILD_FORUM GUILD_FORUM} channel.
     */
    @Nullable List<String> getAppliedTags();

    /**
     * The emoji to show in the add reaction button on a thread in a {@link ChannelType#GUILD_FORUM GUILD_FORUM} channel.
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<DefaultReaction> getDefaultReactionEmoji();

    /**
     * The initial {@link #getRateLimitPerUser() rate_limit_per_user} to set on newly created threads in a channel.
     * this field is copied to the thread at creation time and does not live update.
     */
    @Nullable Integer getDefaultThreadRateLimitPerUser();

    /**
     * The default sort order type used to order posts in {@link ChannelType#GUILD_FORUM GUILD_FORUM} channels. Defaults
     * to {@code null}, which indicates a preferred sort order hasn't been set by a channel admin.
     * @return {@link OptionalValue}
     */
    @NotNull OptionalValue<SortOrderType> getDefaultSortOrder();

    /**
     *
     * @return {@code true} if this instance is a partial channel (not all fields have been retrieved). {@code false} otherwise.
     */
    boolean isPartial();

    /**
     *
     * @return {@code true} if this channel is actually a {@link ThreadChannel thread}. {@code false} otherwise.
     */
    boolean isThread();
}
