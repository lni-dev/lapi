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

package me.linusdev.lapi.api.templates.channel;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.channel.ChannelType;
import me.linusdev.lapi.api.objects.channel.forum.DefaultReaction;
import me.linusdev.lapi.api.objects.channel.forum.SortOrderType;
import me.linusdev.lapi.api.objects.enums.VideoQuality;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrite;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.linusdev.lapi.api.objects.channel.Channel.*;

public class ChannelTemplate implements Template {

    private final @NotNull String name;

    private final @Nullable ChannelType type;
    private final @Nullable String topic;
    private final @Nullable Integer bitrate;
    private final @Nullable Integer userLimit;
    private final @Nullable Integer rateLimitPerUser;
    private final @Nullable Integer position;
    private final @Nullable List<PermissionOverwrite> permissionOverwrites;
    private final @Nullable String parentId;
    private final @Nullable Boolean nsfw;
    private final @Nullable String rtcRegion;
    private final @Nullable VideoQuality videoQualityMode;
    private final @Nullable Integer defaultAutoArchiveDuration;
    private final @Nullable DefaultReaction defaultReactionEmoji;
    private final @Nullable List<ForumTagTemplate> availableTags;
    private final @Nullable SortOrderType defaultSortOrder;

    public ChannelTemplate(@NotNull String name, @Nullable ChannelType type, @Nullable String topic,
                           @Nullable Integer bitrate, @Nullable Integer userLimit, @Nullable Integer rateLimitPerUser,
                           @Nullable Integer position, @Nullable List<PermissionOverwrite> permissionOverwrites,
                           @Nullable String parentId, @Nullable Boolean nsfw, @Nullable String rtcRegion,
                           @Nullable VideoQuality videoQualityMode, @Nullable Integer defaultAutoArchiveDuration,
                           @Nullable DefaultReaction defaultReactionEmoji, @Nullable List<ForumTagTemplate> availableTags,
                           @Nullable SortOrderType defaultSortOrder) {
        this.name = name;
        this.type = type;
        this.topic = topic;
        this.bitrate = bitrate;
        this.userLimit = userLimit;
        this.rateLimitPerUser = rateLimitPerUser;
        this.position = position;
        this.permissionOverwrites = permissionOverwrites;
        this.parentId = parentId;
        this.nsfw = nsfw;
        this.rtcRegion = rtcRegion;
        this.videoQualityMode = videoQualityMode;
        this.defaultAutoArchiveDuration = defaultAutoArchiveDuration;
        this.defaultReactionEmoji = defaultReactionEmoji;
        this.availableTags = availableTags;
        this.defaultSortOrder = defaultSortOrder;
    }

    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(1);

        data.add(NAME_KEY, name);

        data.addIfNotNull(TYPE_KEY, type);
        data.addIfNotNull(TOPIC_KEY, topic);
        data.addIfNotNull(BITRATE_KEY, bitrate);
        data.addIfNotNull(USER_LIMIT_KEY, userLimit);
        data.addIfNotNull(RATE_LIMIT_PER_USER_KEY, rateLimitPerUser);
        data.addIfNotNull(POSITION_KEY, position);
        data.addIfNotNull(PERMISSION_OVERWRITES_KEY, permissionOverwrites);
        data.addIfNotNull(PARENT_ID_KEY, parentId);
        data.addIfNotNull(NSFW_KEY, nsfw);
        data.addIfNotNull(RTC_REGION_KEY, rtcRegion);
        data.addIfNotNull(VIDEO_QUALITY_MODE_KEY, videoQualityMode);
        data.addIfNotNull(DEFAULT_AUTO_ARCHIVE_DURATION_KEY, defaultAutoArchiveDuration);
        data.addIfNotNull(DEFAULT_REACTION_EMOJI_KEY, defaultReactionEmoji);
        data.addIfNotNull(AVAILABLE_TAGS_KEY, availableTags);
        data.addIfNotNull(DEFAULT_SORT_ORDER_KEY, defaultSortOrder);

        return data;
    }
}
