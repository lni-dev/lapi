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
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.GuildChannel;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://discord.com/developers/docs/resources/channel#channel-object-example-store-channel">Example Store Channel</a>
 *
 * Bots can neither send or read messages from this channel type (as it is a store page).
 */
public class GuildStoreChannel extends Channel<GuildStoreChannel> implements GuildChannel {

    protected @NotNull String name;
    protected boolean nsfw;
    protected @NotNull Snowflake guildId;
    protected @Nullable Integer position;
    protected @NotNull PermissionOverwrites permissionOverwrites;
    protected @Nullable Snowflake parentId;

    public GuildStoreChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
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

    public GuildStoreChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        Number position = ((Number) data.get(POSITION_KEY));

        if (name == null) {
            throw new InvalidDataException(data, "field '" + NAME_KEY + "' missing or null in GuildTextChannel with id:" + getId()).addMissingFields(NAME_KEY);
        } else if (guildId == null) {
            throw new InvalidDataException(data, "field '" + GUILD_ID_KEY + "' missing or null in GuildTextChannel with id:" + getId()).addMissingFields(GUILD_ID_KEY);
        }

        this.name = name;
        this.nsfw = (boolean) data.getOrDefaultBoth(NSFW_KEY, false);
        this.guildId = guildId;
        this.position = position == null ? null : position.intValue();;
        this.permissionOverwrites = new PermissionOverwrites(data.getList(PERMISSION_OVERWRITES_KEY, new ArrayList<>()));
        this.parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
    }

    //TODO: make sure messages cannot be sent/read in this channel

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
    public @NotNull GuildStoreChannel copy() {
        return new GuildStoreChannel(lApi,
                Copyable.copy(id),
                type,
                Copyable.copy(name),
                nsfw,
                Copyable.copy(guildId),
                position,
                Copyable.copy(permissionOverwrites),
                Copyable.copy(parentId));
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
    }
}
