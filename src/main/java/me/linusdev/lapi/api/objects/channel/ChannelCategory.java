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
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.GuildChannel;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This represents a {@link ChannelType#GUILD_CATEGORY guild category} in discord.
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-example-channel-category" target="_top">Example Channel Category</a>
 */
public class ChannelCategory extends Channel<ChannelCategory> implements GuildChannel {

    private @NotNull String name;
    private boolean nsfw;
    private @NotNull Snowflake guildId;
    private @Nullable Integer position;
    private @NotNull PermissionOverwrites permissionOverwrites;
    private @Nullable Snowflake parentId;

    public ChannelCategory(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                            @NotNull String name, boolean nsfw,
                            @NotNull Snowflake guildId, @Nullable Integer position, @NotNull PermissionOverwrites permissionOverwrites,
                            @Nullable Snowflake parentId) {
        super(lApi, id, type);

        this.name = name;
        this.nsfw = nsfw;
        this.guildId = guildId;
        this.position = position;
        this.permissionOverwrites = permissionOverwrites;
        this.parentId = parentId;

    }

    public ChannelCategory(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        Number position = ((Number) data.get(POSITION_KEY));

        if (name == null) {
            throw new InvalidDataException(data, "field '" + NAME_KEY + "' missing or null in ChannelCategory with id:" + getId()).addMissingFields(NAME_KEY);
        } else if (guildId == null) {
            throw new InvalidDataException(data, "field '" + GUILD_ID_KEY + "' missing or null in ChannelCategory with id:" + getId()).addMissingFields(GUILD_ID_KEY);
        }

        this.name = name;
        this.nsfw = (boolean) data.getOrDefaultBoth(NSFW_KEY, false);
        this.guildId = guildId;
        this.position = position == null ? null : position.intValue();
        this.permissionOverwrites = new PermissionOverwrites(data.getList(PERMISSION_OVERWRITES_KEY, new ArrayList<>()));
        this.parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
    }

    @Override
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
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
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean getNsfw() {
        return nsfw;
    }

    @Override
    public @Nullable Snowflake getParentIdAsSnowflake() {
        return parentId;
    }

    @Override
    public @NotNull ChannelCategory copy() {
        return new ChannelCategory(lApi,
                Copyable.copy(id),
                type,
                Copyable.copy(name),
                nsfw,
                Copyable.copy(guildId),
                position,
                Copyable.copy(permissionOverwrites),
                Copyable.copy(parentId)
        );
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        super.updateSelfByData(data);

        data.processIfContained(NAME_KEY, (String str) -> this.name = str);
        data.processIfContained(NSFW_KEY, (Boolean bool) -> this.nsfw = bool);
        //guildId should never change
        data.processIfContained(POSITION_KEY, (Number num) -> {if(num != null) this.position = num.intValue();});

        List<Object> array = data.getList(PERMISSION_OVERWRITES_KEY);
        if(array != null) this.permissionOverwrites = new PermissionOverwrites(array);

        data.processIfContained(PARENT_ID_KEY, (String str) -> this.parentId = Snowflake.fromString(str));
    }
}
