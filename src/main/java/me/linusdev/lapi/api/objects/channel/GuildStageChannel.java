/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.GuildStageChannelAbstract;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import me.linusdev.lapi.api.objects.enums.VideoQuality;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildStageChannel extends GuildVoiceChannel implements GuildStageChannelAbstract {

    protected @Nullable String topic;

    public GuildStageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull String name,
                             boolean nsfw, @NotNull Snowflake guildId, @Nullable Integer position, @NotNull PermissionOverwrites permissionOverwrites, @Nullable Snowflake parentId, int bitRate, int userLimit, @Nullable String rtcRegion, @NotNull VideoQuality videoQualityMode, @Nullable String topic) {
        super(lApi, id, type, name, nsfw, guildId, position, permissionOverwrites, parentId, bitRate, userLimit, rtcRegion, videoQualityMode);
        this.topic = topic;
    }

    public GuildStageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) throws InvalidDataException {
        super(lApi, id, type, data);
        this.topic = (String) data.get(Channel.TOPIC_KEY, null);
    }

    @Override
    public @Nullable String getTopic() {
        return topic;
    }

    @Override
    public @NotNull GuildStageChannel copy() {
        return new GuildStageChannel(lApi,
                Copyable.copy(id),
                type,
                Copyable.copy(name),
                nsfw,
                Copyable.copy(guildId),
                position,
                Copyable.copy(permissionOverwrites),
                Copyable.copy(parentId),
                bitRate,
                userLimit,
                Copyable.copy(rtcRegion),
                videoQualityMode,
                Copyable.copy(topic));
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        super.updateSelfByData(data);
        data.processIfContained(TOPIC_KEY, (String str) -> this.topic = str);
    }
}
