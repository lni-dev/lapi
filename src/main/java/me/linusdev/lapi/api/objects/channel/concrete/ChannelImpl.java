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

package me.linusdev.lapi.api.objects.channel.concrete;

import me.linusdev.data.OptionalValue;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.channel.AbstractChannel;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.channel.ChannelType;
import me.linusdev.lapi.api.objects.channel.forum.DefaultReaction;
import me.linusdev.lapi.api.objects.channel.forum.ForumTag;
import me.linusdev.lapi.api.objects.channel.forum.SortOrderType;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.enums.VideoQuality;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrite;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see Channel
 */
public class ChannelImpl extends AbstractChannel {

    public ChannelImpl(@NotNull LApi lApi, @NotNull String id, @NotNull ChannelType type, @Nullable String guildId, @Nullable Integer position, @Nullable List<PermissionOverwrite> permissionOverwrites, @NotNull OptionalValue<String> nameOptional, @NotNull OptionalValue<String> topicOptional, @Nullable Boolean nsfw, @NotNull OptionalValue<String> lastMessageIdOptional, @Nullable Integer bitrate, @Nullable Integer userLimit, @Nullable Integer rateLimitPerUser, @Nullable List<User> recipients, @NotNull OptionalValue<String> iconOptional, @Nullable String ownerId, @Nullable String applicationId, @NotNull OptionalValue<String> parentIdOptional, @NotNull OptionalValue<ISO8601Timestamp> lastPinTimestampOptional, @NotNull OptionalValue<String> rtcRegionOptional, @Nullable VideoQuality videoQualityMode, @Nullable Integer messageCount, @Nullable Integer memberCount, @Nullable ThreadMetadata threadMetadata, @Nullable ThreadMember member, @Nullable Integer defaultAutoArchiveDuration, @Nullable Permissions permissions, @Nullable Integer flags, @Nullable Integer totalMessageSent, @Nullable List<ForumTag> availableTags, @Nullable List<String> appliedTags, @NotNull OptionalValue<DefaultReaction> defaultReactionEmojiOptional, @Nullable Integer defaultThreadRateLimitPerUser, @NotNull OptionalValue<SortOrderType> defaultSortOrderOptional) {
        super(lApi, id, type, guildId, position, permissionOverwrites, nameOptional, topicOptional, nsfw, lastMessageIdOptional, bitrate, userLimit, rateLimitPerUser, recipients, iconOptional, ownerId, applicationId, parentIdOptional, lastPinTimestampOptional, rtcRegionOptional, videoQualityMode, messageCount, memberCount, threadMetadata, member, defaultAutoArchiveDuration, permissions, flags, totalMessageSent, availableTags, appliedTags, defaultReactionEmojiOptional, defaultThreadRateLimitPerUser, defaultSortOrderOptional);
    }

    public ChannelImpl(@NotNull ChannelType type, @NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        super(type, lApi, data);
    }

    @Override
    public boolean isPartial() {
        return false;
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public @NotNull Channel copy() {
        return new ChannelImpl(
                lApi,
                Copyable.copy(id),
                type,
                Copyable.copy(guildId),
                position,
                Copyable.copyListDeep(permissionOverwrites),
                nameOptional,
                topicOptional,
                nsfw,
                lastMessageIdOptional,
                bitrate,
                userLimit,
                rateLimitPerUser,
                Copyable.copyListFlat(recipients),
                iconOptional,
                Copyable.copy(ownerId),
                Copyable.copy(applicationId),
                parentIdOptional,
                lastPinTimestampOptional,
                rtcRegionOptional,
                videoQualityMode,
                messageCount,
                memberCount,
                Copyable.copy(threadMetadata),
                Copyable.copy(member),
                defaultAutoArchiveDuration,
                Copyable.copy(permissions),
                flags,
                totalMessageSent,
                Copyable.copyListFlat(availableTags),
                Copyable.copyListFlat(appliedTags),
                defaultReactionEmojiOptional,
                defaultThreadRateLimitPerUser,
                defaultSortOrderOptional
        );
    }
}
