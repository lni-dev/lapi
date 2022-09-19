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
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import me.linusdev.lapi.api.objects.enums.VideoQuality;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.GuildVoiceChannelAbstract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuildVoiceChannel extends Channel<GuildVoiceChannel> implements GuildVoiceChannelAbstract {

    protected @NotNull String name;
    protected boolean nsfw;
    protected @NotNull Snowflake guildId;
    protected @Nullable Integer position;
    protected @NotNull PermissionOverwrites permissionOverwrites;
    protected @Nullable Snowflake parentId;
    protected int bitRate;
    protected int userLimit;
    protected @Nullable String rtcRegion;
    protected @NotNull VideoQuality videoQualityMode;

    public GuildVoiceChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                             @NotNull String name, boolean nsfw, @NotNull Snowflake guildId,
                             @Nullable Integer position, @NotNull PermissionOverwrites permissionOverwrites, @Nullable Snowflake parentId,
                             int bitRate, int userLimit, @Nullable String rtcRegion,
                             @NotNull VideoQuality videoQualityMode) {
        super(lApi, id, type);

        this.name = name;
        this.nsfw = nsfw;
        this.guildId = guildId;
        this.position = position;
        this.permissionOverwrites = permissionOverwrites;
        this.parentId = parentId;
        this.bitRate = bitRate;
        this.userLimit = userLimit;
        this.rtcRegion = rtcRegion;
        this.videoQualityMode = videoQualityMode;

    }

    public GuildVoiceChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        Number position = ((Number) data.get(POSITION_KEY));

        if (name == null) {
            throw new InvalidDataException(data, "field '" + NAME_KEY + "' missing or null in GuildVoiceChannel with id:" + getId()).addMissingFields(NAME_KEY);
        } else if (guildId == null) {
            throw new InvalidDataException(data, "field '" + GUILD_ID_KEY + "' missing or null in GuildVoiceChannel with id:" + getId()).addMissingFields(GUILD_ID_KEY);
        }

        this.name = name;
        this.nsfw = (boolean) data.getOrDefaultBoth(NSFW_KEY, false);
        this.guildId = guildId;
        this.position = position == null ? null : position.intValue();
        this.permissionOverwrites = new PermissionOverwrites(data.getList(PERMISSION_OVERWRITES_KEY, new ArrayList<>()));
        this.parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
        this.bitRate = ((Number) data.getOrDefaultBoth(BITRATE_KEY, -1)).intValue();
        this.userLimit = ((Number) data.getOrDefaultBoth(USER_LIMIT_KEY, 0)).intValue();
        this.rtcRegion = (String) data.getOrDefaultBoth(RTC_REGION_KEY, null);
        this.videoQualityMode = VideoQuality.fromId((Number) data.get(VIDEO_QUALITY_MODE_KEY));

    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean getNsfw() {
        return nsfw;
    }

    @Override
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    @Override
    public @NotNull String getGuildId() {
        return getGuildIdAsSnowflake().asString();
    }

    @Override
    public @Nullable Integer getPosition() {
        return position;
    }

    @Override
    public @NotNull PermissionOverwrites getPermissionOverwrites() {
        return permissionOverwrites;
    }

    @Override
    public @Nullable Snowflake getParentIdAsSnowflake() {
        return parentId;
    }

    @Override
    public int getBitRate() {
        return bitRate;
    }

    @Override
    public int getUserLimit() {
        return userLimit;
    }

    @Override
    public @Nullable String getRTCRegion() {
        return rtcRegion;
    }


    @Override
    public @NotNull VideoQuality getVideoQualityMode() {
        return videoQualityMode;
    }

    @Override
    public @NotNull GuildVoiceChannel copy() {
        return new GuildVoiceChannel(lApi,
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
                videoQualityMode);
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        super.updateSelfByData(data);
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
    }
}
