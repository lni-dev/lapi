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

import me.linusdev.data.OptionalValue;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.enums.VideoQuality;
import me.linusdev.lapi.api.objects.nchannel.forum.DefaultReaction;
import me.linusdev.lapi.api.objects.nchannel.forum.ForumTag;
import me.linusdev.lapi.api.objects.nchannel.forum.SortOrderType;
import me.linusdev.lapi.api.objects.nchannel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.nchannel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrite;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractChannel implements Channel {

    protected final @NotNull LApi lApi;

    protected final @NotNull String id;
    protected final @NotNull ChannelType type;
    protected final @Nullable String guildId;
    protected final @Nullable Integer position;
    protected final @Nullable List<PermissionOverwrite> permissionOverwrites;
    protected final @NotNull OptionalValue<String> nameOptional;
    protected final @NotNull OptionalValue<String> topicOptional;
    protected final @Nullable Boolean nsfw;
    protected final @NotNull OptionalValue<String> lastMessageIdOptional;
    protected final @Nullable Integer bitrate;
    protected final @Nullable Integer userLimit;
    protected final @Nullable Integer rateLimitPerUser;
    protected final @Nullable List<User> recipients;
    protected final @NotNull OptionalValue<String> iconOptional;
    protected final @Nullable String ownerId;
    protected final @Nullable String applicationId;
    protected final @NotNull OptionalValue<String> parentIdOptional;
    protected final @NotNull OptionalValue<ISO8601Timestamp> lastPinTimestampOptional;
    protected final @NotNull OptionalValue<String> rtcRegionOptional;
    protected final @Nullable VideoQuality videoQualityMode;
    protected final @Nullable Integer messageCount;
    protected final @Nullable Integer memberCount;
    protected final @Nullable ThreadMetadata threadMetadata;
    protected final @Nullable ThreadMember member;
    protected final @Nullable Integer defaultAutoArchiveDuration;
    protected final @Nullable Permissions permissions;
    protected final @Nullable Integer flags;
    protected final @Nullable Integer totalMessageSent;
    protected final @Nullable List<ForumTag> availableTags;
    protected final @Nullable List<String> appliedTags;
    protected final @NotNull OptionalValue<DefaultReaction> defaultReactionEmojiOptional;
    protected final @Nullable Integer defaultThreadRateLimitPerUser;
    protected final @NotNull OptionalValue<SortOrderType> defaultSortOrderOptional;


    protected AbstractChannel(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        this.lApi = lApi;

        this.id = data.getAs(ID_KEY);
        this.type = data.getAndConvert(TYPE_KEY, ChannelType::fromNumber);
        this.guildId = data.getAs(GUILD_ID_KEY);
        this.position = data.getAs(POSITION_KEY);
        this.permissionOverwrites = data.getListAndConvertWithException(PERMISSION_OVERWRITES_KEY, PermissionOverwrite::fromData);
        this.nameOptional = data.getOptionalValue(NAME_KEY);
        this.topicOptional = data.getOptionalValue(TOPIC_KEY);
        this.nsfw = data.getAs(NSFW_KEY);
        this.lastMessageIdOptional = data.getOptionalValue(LAST_MESSAGE_ID_KEY);
        this.bitrate = data.getAs(BITRATE_KEY);
        this.userLimit = data.getAs(USER_LIMIT_KEY);
        this.rateLimitPerUser = data.getAs(RATE_LIMIT_PER_USER_KEY);
        this.recipients = data.getListAndConvertWithException(RECIPIENTS_KEY, (SOData c) -> User.fromData(lApi, c));
        this.iconOptional = data.getOptionalValue(ICON_KEY);
        this.ownerId = data.getAs(OWNER_ID_KEY);
        this.applicationId = data.getAs(APPLICATION_ID_KEY);
        this.parentIdOptional = data.getOptionalValue(PARENT_ID_KEY);
        this.lastPinTimestampOptional = data.getOptionalValueAndConvert(LAST_PIN_TIMESTAMP_KEY, ISO8601Timestamp::fromString);
        this.rtcRegionOptional = data.getOptionalValue(RTC_REGION_KEY);
        this.videoQualityMode = data.getAndConvert(VIDEO_QUALITY_MODE_KEY, VideoQuality::fromId);
        this.messageCount = data.getAs(MESSAGE_COUNT_KEY);
        this.memberCount = data.getAs(MEMBER_COUNT_KEY);
        this.threadMetadata = data.getAndConvertWithException(THREAD_METADATA_KEY, ThreadMetadata::new);
        this.member = data.getAndConvertWithException(MEMBER_KEY, ThreadMember::fromData);
        this.defaultAutoArchiveDuration = data.getAs(DEFAULT_AUTO_ARCHIVE_DURATION_KEY);
        this.permissions = data.getAndConvert(PERMISSIONS_KEY, Permissions::ofString);
        this.flags = data.getAs(FLAGS_KEY);
        this.totalMessageSent = data.getAs(TOTAL_MESSAGE_SENT_KEY);
        this.availableTags = data.getListAndConvertWithException(AVAILABLE_TAGS_KEY, ForumTag::fromData);
        this.appliedTags = data.getListAndConvert(APPLIED_TAGS_KEY, (String c) -> c);
        this.defaultReactionEmojiOptional = data.getOptionalValueAndConvert(DEFAULT_REACTION_EMOJI_KEY, DefaultReaction::fromData);
        this.defaultThreadRateLimitPerUser = data.getAs(DEFAULT_THREAD_RATE_LIMIT_PER_USER_KEY);
        this.defaultSortOrderOptional = data.getOptionalValueAndConvert(DEFAULT_SORT_ORDER_KEY, SortOrderType::fromValue);


        //TODO: fill variables
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull ChannelType getType() {
        return type;
    }

    @Override
    public @Nullable String getGuild() {
        return null;
    }

    @Override
    public @Nullable Integer getPosition() {
        return null;
    }

    @Override
    public @Nullable List<PermissionOverwrite> getPermissionOverwrites() {
        return null;
    }

    @Override
    public @NotNull OptionalValue<String> getName() {
        return nameOptional;
    }

    @Override
    public @NotNull OptionalValue<String> getTopic() {
        return topicOptional;
    }

    @Override
    public @Nullable Boolean isNSFW() {
        return nsfw;
    }

    @Override
    public @NotNull OptionalValue<String> getLastMessageId() {
        return lastMessageIdOptional;
    }

    @Override
    public @Nullable Integer getBitrate() {
        return bitrate;
    }

    @Override
    public @Nullable Integer getUserLimit() {
        return userLimit;
    }

    @Override
    public @Nullable Integer getRateLimitPerUser() {
        return rateLimitPerUser;
    }

    @Override
    public @Nullable List<User> getRecipients() {
        return recipients;
    }

    @Override
    public @NotNull OptionalValue<String> getIcon() {
        return iconOptional;
    }

    @Override
    public @Nullable String getOwnerId() {
        return ownerId;
    }

    @Override
    public @Nullable String getApplicationId() {
        return applicationId;
    }

    @Override
    public @NotNull OptionalValue<String> getParentId() {
        return parentIdOptional;
    }

    @Override
    public @NotNull OptionalValue<ISO8601Timestamp> getLastPinTimestamp() {
        return lastPinTimestampOptional;
    }

    @Override
    public @NotNull OptionalValue<String> getRTCRegion() {
        return rtcRegionOptional;
    }

    @Override
    public @Nullable VideoQuality getVideoQualityMode() {
        return videoQualityMode;
    }

    @Override
    public @Nullable Integer getMessageCount() {
        return messageCount;
    }

    @Override
    public @Nullable Integer getMemberCount() {
        return memberCount;
    }

    @Override
    public @Nullable ThreadMetadata getThreadMetadata() {
        return threadMetadata;
    }

    @Override
    public @Nullable ThreadMember getMember() {
        return member;
    }

    @Override
    public @Nullable Integer getDefaultAutoArchiveDuration() {
        return defaultAutoArchiveDuration;
    }

    @Override
    public @Nullable Permissions getPermissions() {
        return permissions;
    }

    @Override
    public @Nullable Integer getFlags() {
        return flags;
    }

    @Override
    public @Nullable Integer totalMessageSent() {
        return totalMessageSent;
    }

    @Override
    public @Nullable List<ForumTag> getAvailableTags() {
        return availableTags;
    }

    @Override
    public @Nullable List<String> getAppliedTags() {
        return appliedTags;
    }

    @Override
    public @NotNull OptionalValue<DefaultReaction> getDefaultReactionEmoji() {
        return defaultReactionEmojiOptional;
    }

    @Override
    public @Nullable Integer getDefaultThreadRateLimitPerUser() {
        return defaultThreadRateLimitPerUser;
    }

    @Override
    public @NotNull OptionalValue<SortOrderType> getDefaultSortOrder() {
        return defaultSortOrderOptional;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(20);

        //TODO: add fields

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
