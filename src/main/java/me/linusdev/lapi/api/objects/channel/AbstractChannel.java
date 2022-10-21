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
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
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
    protected @Nullable ThreadMember member;
    protected final @Nullable Integer defaultAutoArchiveDuration;
    protected final @Nullable Permissions permissions;
    protected final @Nullable Integer flags;
    protected final @Nullable Integer totalMessageSent;
    protected final @Nullable List<ForumTag> availableTags;
    protected final @Nullable List<String> appliedTags;
    protected final @NotNull OptionalValue<DefaultReaction> defaultReactionEmojiOptional;
    protected final @Nullable Integer defaultThreadRateLimitPerUser;
    protected final @NotNull OptionalValue<SortOrderType> defaultSortOrderOptional;


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

    @Override
    public @Nullable ThreadMember updateMember(@Nullable ThreadMember newMember) {
        ThreadMember old = member;
        member = newMember;
        return old;
    }

    @Override
    public @NotNull Channel copy() {
        return null;
    }

    @Override
    public boolean checkIfChanged(@NotNull SOData data) {
        return Channel.super.checkIfChanged(data);
    }

    @Override
    public void updateSelfByData(@NotNull SOData data) throws InvalidDataException {
        /*

        List<Object> rs = data.getList(RECIPIENTS_KEY);
        if(rs != null) {
            this.recipients = new Recipient[rs.size()];
            int i = 0;
            for(Object d : rs) this.recipients[i++] = new Recipient(lApi,(SOData) d);
        }

        data.processIfContained(LAST_MESSAGE_ID_KEY, (String str) -> this.lastMessageId = Snowflake.fromString(str));
        data.processIfContained(LAST_PIN_TIMESTAMP_KEY, (String str) -> this.lastPinTimestamp = ISO8601Timestamp.fromString(str));
        data.processIfContained(ICON_KEY, (String str) -> this.iconHash = str);
        data.processIfContained(OWNER_ID_KEY, (String str) -> this.ownerId = Snowflake.fromString(str));
        data.processIfContained(APPLICATION_ID_KEY, (String str) -> this.applicationId = Snowflake.fromString(str));


        data.processIfContained(TOPIC_KEY, (String str) -> this.topic = str);


        data.processIfContained(NAME_KEY, (String str) -> this.name = str);
        data.processIfContained(NSFW_KEY, (Boolean bool) -> {if (bool != null) this.nsfw = bool;});
        //guildId may not change
        data.processIfContained(POSITION_KEY, (Number num) -> {if(num != null) this.position = num.intValue();});

        List<Object> array = data.getList(PERMISSION_OVERWRITES_KEY);
        if(array != null) this.permissionOverwrites = new PermissionOverwrites(array);

        data.processIfContained(PARENT_ID_KEY, (String str) -> this.parentId = Snowflake.fromString(str));



        data.processIfContained(NAME_KEY, (String str) -> this.name = str);
        data.processIfContained(TOPIC_KEY, (String str) -> this.topic = str);
        data.processIfContained(NSFW_KEY, (Boolean bool) -> this.nsfw = bool);
        //guildId should never change
        data.processIfContained(POSITION_KEY, (Number num) -> {if(num != null) this.position = num.intValue();});

        List<Object> array = data.getList(PERMISSION_OVERWRITES_KEY);
        if(array != null) this.permissionOverwrites = new PermissionOverwrites(array);

        data.processIfContained(PARENT_ID_KEY, (String str) -> this.parentId = Snowflake.fromString(str));
        data.processIfContained(RATE_LIMIT_PER_USER_KEY, (Number num) -> {if(num != null)this.rateLimitPerUser = num.intValue();});
        data.processIfContained(LAST_MESSAGE_ID_KEY, (String str) -> this.lastMessageId = Snowflake.fromString(str));
        data.processIfContained(LAST_PIN_TIMESTAMP_KEY, (String str) -> this.lastPinTimestamp = ISO8601Timestamp.fromString(str));



        data.processIfContained(NAME_KEY, (String str) -> this.name = str);
        data.processIfContained(NSFW_KEY, (Boolean bool) -> {if (bool != null) this.nsfw = bool;});
        //guildId may not change
        data.processIfContained(POSITION_KEY, (Number num) -> {if(num != null) this.position = num.intValue();});

        List<Object> array = data.getList(PERMISSION_OVERWRITES_KEY);
        if(array != null) this.permissionOverwrites = new PermissionOverwrites(array);

        data.processIfContained(PARENT_ID_KEY, (String str) -> this.parentId = Snowflake.fromString(str));
        data.processIfContained(BITRATE_KEY, (Number num) -> {if(num != null) this.bitRate = num.intValue();});
        data.processIfContained(USER_LIMIT_KEY, (Number num) -> {if(num != null) this.userLimit = num.intValue();});
        data.processIfContained(RTC_REGION_KEY, (String str) -> this.rtcRegion = str);
        data.processIfContained(VIDEO_QUALITY_MODE_KEY, (Number num) -> {if(num != null) this.videoQualityMode = VideoQuality.fromId(num);});

        */
    }
}
