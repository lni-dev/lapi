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

package me.linusdev.lapi.api.objects.channel;

import me.linusdev.data.OptionalValue;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.enums.VideoQuality;
import me.linusdev.lapi.api.objects.channel.forum.DefaultReaction;
import me.linusdev.lapi.api.objects.channel.forum.ForumTag;
import me.linusdev.lapi.api.objects.channel.forum.SortOrderType;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrite;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractChannel implements Channel {

    protected final @NotNull LApi lApi;

    protected final @NotNull String id;
    protected @NotNull ChannelType type;
    protected final @Nullable String guildId;
    protected @Nullable Integer position;
    protected @Nullable List<PermissionOverwrite> permissionOverwrites;
    protected @NotNull OptionalValue<String> nameOptional;
    protected @NotNull OptionalValue<String> topicOptional;
    protected @Nullable Boolean nsfw;
    protected @NotNull OptionalValue<String> lastMessageIdOptional;
    protected @Nullable Integer bitrate;
    protected @Nullable Integer userLimit;
    protected @Nullable Integer rateLimitPerUser;
    protected @Nullable List<User> recipients;
    protected @NotNull OptionalValue<String> iconOptional;
    protected @Nullable String ownerId;
    protected @Nullable String applicationId;
    protected @NotNull OptionalValue<String> parentIdOptional;
    protected @NotNull OptionalValue<ISO8601Timestamp> lastPinTimestampOptional;
    protected @NotNull OptionalValue<String> rtcRegionOptional;
    protected @Nullable VideoQuality videoQualityMode;
    protected @Nullable Integer messageCount;
    protected @Nullable Integer memberCount;
    protected @Nullable ThreadMetadata threadMetadata;
    protected @Nullable ThreadMember member;
    protected @Nullable Integer defaultAutoArchiveDuration;
    protected @Nullable Permissions permissions;
    protected @Nullable Integer flags;
    protected @Nullable Integer totalMessageSent;
    protected @Nullable List<ForumTag> availableTags;
    protected @Nullable List<String> appliedTags;
    protected @NotNull OptionalValue<DefaultReaction> defaultReactionEmojiOptional;
    protected @Nullable Integer defaultThreadRateLimitPerUser;
    protected @NotNull OptionalValue<SortOrderType> defaultSortOrderOptional;

    public AbstractChannel(
            @NotNull LApi lApi, @NotNull String id, @NotNull ChannelType type, @Nullable String guildId,
            @Nullable Integer position, @Nullable List<PermissionOverwrite> permissionOverwrites,
            @NotNull OptionalValue<String> nameOptional, @NotNull OptionalValue<String> topicOptional,
            @Nullable Boolean nsfw, @NotNull OptionalValue<String> lastMessageIdOptional,
            @Nullable Integer bitrate, @Nullable Integer userLimit, @Nullable Integer rateLimitPerUser,
            @Nullable List<User> recipients, @NotNull OptionalValue<String> iconOptional, @Nullable String ownerId,
            @Nullable String applicationId, @NotNull OptionalValue<String> parentIdOptional,
            @NotNull OptionalValue<ISO8601Timestamp> lastPinTimestampOptional,
            @NotNull OptionalValue<String> rtcRegionOptional, @Nullable VideoQuality videoQualityMode,
            @Nullable Integer messageCount, @Nullable Integer memberCount, @Nullable ThreadMetadata threadMetadata,
            @Nullable ThreadMember member, @Nullable Integer defaultAutoArchiveDuration,
            @Nullable Permissions permissions, @Nullable Integer flags, @Nullable Integer totalMessageSent,
            @Nullable List<ForumTag> availableTags, @Nullable List<String> appliedTags,
            @NotNull OptionalValue<DefaultReaction> defaultReactionEmojiOptional,
            @Nullable Integer defaultThreadRateLimitPerUser,
            @NotNull OptionalValue<SortOrderType> defaultSortOrderOptional
    )
    {
        this.lApi = lApi;
        this.id = id;
        this.type = type;
        this.guildId = guildId;
        this.position = position;
        this.permissionOverwrites = permissionOverwrites;
        this.nameOptional = nameOptional;
        this.topicOptional = topicOptional;
        this.nsfw = nsfw;
        this.lastMessageIdOptional = lastMessageIdOptional;
        this.bitrate = bitrate;
        this.userLimit = userLimit;
        this.rateLimitPerUser = rateLimitPerUser;
        this.recipients = recipients;
        this.iconOptional = iconOptional;
        this.ownerId = ownerId;
        this.applicationId = applicationId;
        this.parentIdOptional = parentIdOptional;
        this.lastPinTimestampOptional = lastPinTimestampOptional;
        this.rtcRegionOptional = rtcRegionOptional;
        this.videoQualityMode = videoQualityMode;
        this.messageCount = messageCount;
        this.memberCount = memberCount;
        this.threadMetadata = threadMetadata;
        this.member = member;
        this.defaultAutoArchiveDuration = defaultAutoArchiveDuration;
        this.permissions = permissions;
        this.flags = flags;
        this.totalMessageSent = totalMessageSent;
        this.availableTags = availableTags;
        this.appliedTags = appliedTags;
        this.defaultReactionEmojiOptional = defaultReactionEmojiOptional;
        this.defaultThreadRateLimitPerUser = defaultThreadRateLimitPerUser;
        this.defaultSortOrderOptional = defaultSortOrderOptional;
    }

    protected AbstractChannel(@NotNull ChannelType type, @NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        this.lApi = lApi;

        this.id = data.getAsAndRequireNotNull(ID_KEY, InvalidDataException.SUPPLIER);
        this.type = type;
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
        SOData data = SOData.newOrderedDataWithKnownSize(25);

        data.add(ID_KEY, id);
        data.add(TYPE_KEY, type);
        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.addIfNotNull(POSITION_KEY, position);
        data.addIfNotNull(PERMISSION_OVERWRITES_KEY, permissionOverwrites);
        data.addIfOptionalExists(NAME_KEY, nameOptional);
        data.addIfOptionalExists(TOPIC_KEY, topicOptional);
        data.addIfNotNull(NSFW_KEY, nsfw);
        data.addIfOptionalExists(LAST_MESSAGE_ID_KEY, lastMessageIdOptional);
        data.addIfNotNull(BITRATE_KEY, bitrate);
        data.addIfNotNull(USER_LIMIT_KEY, userLimit);
        data.addIfNotNull(RATE_LIMIT_PER_USER_KEY, rateLimitPerUser);
        data.addIfNotNull(RECIPIENTS_KEY, recipients);
        data.addIfOptionalExists(ICON_KEY, iconOptional);
        data.addIfNotNull(OWNER_ID_KEY, ownerId);
        data.addIfNotNull(APPLICATION_ID_KEY, applicationId);
        data.addIfOptionalExists(PARENT_ID_KEY, parentIdOptional);
        data.addIfOptionalExists(LAST_PIN_TIMESTAMP_KEY, lastPinTimestampOptional);
        data.addIfOptionalExists(RTC_REGION_KEY, rtcRegionOptional);
        data.addIfNotNull(VIDEO_QUALITY_MODE_KEY, videoQualityMode);
        data.addIfNotNull(MESSAGE_COUNT_KEY, messageCount);
        data.addIfNotNull(MEMBER_COUNT_KEY, memberCount);
        data.addIfNotNull(THREAD_METADATA_KEY, threadMetadata);
        data.addIfNotNull(MEMBER_KEY, member);
        data.addIfNotNull(DEFAULT_AUTO_ARCHIVE_DURATION_KEY, defaultAutoArchiveDuration);
        data.addIfNotNull(PERMISSIONS_KEY, permissions);
        data.addIfNotNull(FLAGS_KEY, flags);
        data.addIfNotNull(TOTAL_MESSAGE_SENT_KEY, totalMessageSent);
        data.addIfNotNull(AVAILABLE_TAGS_KEY, availableTags);
        data.addIfNotNull(APPLIED_TAGS_KEY, appliedTags);
        data.addIfOptionalExists(DEFAULT_REACTION_EMOJI_KEY, defaultReactionEmojiOptional);
        data.addIfNotNull(DEFAULT_THREAD_RATE_LIMIT_PER_USER_KEY, defaultThreadRateLimitPerUser);
        data.addIfOptionalExists(DEFAULT_SORT_ORDER_KEY, defaultSortOrderOptional);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @ApiStatus.Internal
    @Override
    public @Nullable ThreadMember updateMember(@Nullable ThreadMember newMember) {
        ThreadMember old = member;
        member = newMember;
        return old;
    }

    @Override
    public boolean checkIfChanged(@NotNull SOData data) {
        return Channel.super.checkIfChanged(data);
    }

    @Override
    public void updateSelfByData(@NotNull SOData data) throws InvalidDataException {
        //id will never change
        data.processIfContained(TYPE_KEY, (Number num) -> this.type = ChannelType.fromNumber(num));
        //guild id will never change
        data.processIfContainedAndRequireNotNull(POSITION_KEY, (Number num) -> this.position = num.intValue(), InvalidDataException.SUPPLIER);
        data.getContainer(PERMISSION_OVERWRITES_KEY).ifExists().requireNotNull(InvalidDataException.SUPPLIER)
                .asList().castAndConvertWithException(PermissionOverwrite::fromData).process(list -> this.permissionOverwrites = list);
        data.processIfContained(NAME_KEY, (String s ) -> this.nameOptional = OptionalValue.of(s));
        data.processIfContained(TOPIC_KEY, (String s) -> this.topicOptional = OptionalValue.of(s));
        data.processIfContainedAndRequireNotNull(NSFW_KEY, (Boolean b) -> this.nsfw = b, InvalidDataException.SUPPLIER);
        data.processIfContained(LAST_MESSAGE_ID_KEY, (String s) -> this.lastMessageIdOptional = OptionalValue.of(s));
        data.processIfContainedAndRequireNotNull(BITRATE_KEY, (Number num) -> this.bitrate = num.intValue(), InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(USER_LIMIT_KEY, (Number num) -> this.userLimit = num.intValue(), InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(RATE_LIMIT_PER_USER_KEY, (Number num) -> this.rateLimitPerUser = num.intValue(), InvalidDataException.SUPPLIER);
        data.getContainer(RECIPIENTS_KEY).ifExists().requireNotNull(InvalidDataException.SUPPLIER)
                .asList().castAndConvertWithException((SOData c) -> User.fromData(lApi, c)).process(users -> this.recipients = users);
        data.processIfContained(ICON_KEY, (String s) -> this.iconOptional = OptionalValue.of(s));
        data.processIfContainedAndRequireNotNull(OWNER_ID_KEY, (String s) -> this.ownerId = s, InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(APPLICATION_ID_KEY, (String s) -> this.applicationId = s, InvalidDataException.SUPPLIER);
        data.processIfContained(PARENT_ID_KEY, (String s) -> this.parentIdOptional = OptionalValue.of(s));
        data.convertAndProcessIfContained(LAST_PIN_TIMESTAMP_KEY, ISO8601Timestamp::fromString, t -> this.lastPinTimestampOptional = OptionalValue.of(t));
        data.processIfContained(RTC_REGION_KEY, (String s) -> this.rtcRegionOptional = OptionalValue.of(s));
        data.convertAndProcessIfContainedAndRequireNotNull(VIDEO_QUALITY_MODE_KEY, VideoQuality::fromId, vq -> this.videoQualityMode = vq, InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(MESSAGE_COUNT_KEY, (Number num) -> this.messageCount = num.intValue(), InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(MEMBER_COUNT_KEY, (Number num) -> this.memberCount = num.intValue(), InvalidDataException.SUPPLIER);
        data.convertWithExceptionAndProcessIfContainedAndRequireNotNull(THREAD_METADATA_KEY, ThreadMetadata::new, tM -> this.threadMetadata = tM, InvalidDataException.SUPPLIER);
        data.convertWithExceptionAndProcessIfContainedAndRequireNotNull(MEMBER_KEY, ThreadMember::fromData, tM -> this.member = tM, InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(DEFAULT_AUTO_ARCHIVE_DURATION_KEY, (Number num) -> this.defaultAutoArchiveDuration = num.intValue(), InvalidDataException.SUPPLIER);
        data.convertWithExceptionAndProcessIfContainedAndRequireNotNull(PERMISSIONS_KEY, Permissions::ofString, p -> this.permissions = p, InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(FLAGS_KEY, (Number num) -> this.flags = num.intValue(), InvalidDataException.SUPPLIER);
        data.processIfContainedAndRequireNotNull(TOTAL_MESSAGE_SENT_KEY, (Number num) -> this.totalMessageSent = num.intValue(), InvalidDataException.SUPPLIER);
        data.getContainer(AVAILABLE_TAGS_KEY).ifExists().requireNotNull(InvalidDataException.SUPPLIER)
                .asList().castAndConvertWithException(ForumTag::fromData).process(list -> this.availableTags = list);
        data.getContainer(APPLIED_TAGS_KEY).ifExists().requireNotNull(InvalidDataException.SUPPLIER).asList().<String>cast().process((List<String> list) -> this.appliedTags = list);
        data.convertAndProcessIfContained(DEFAULT_REACTION_EMOJI_KEY, DefaultReaction::fromData, dr -> this.defaultReactionEmojiOptional = OptionalValue.of(dr));
        data.processIfContainedAndRequireNotNull(DEFAULT_THREAD_RATE_LIMIT_PER_USER_KEY, (Number num) -> this.defaultThreadRateLimitPerUser = num.intValue(), InvalidDataException.SUPPLIER);
        data.convertAndProcessIfContained(DEFAULT_SORT_ORDER_KEY, SortOrderType::fromValue, sot -> this.defaultSortOrderOptional = OptionalValue.of(sot));
    }
}
