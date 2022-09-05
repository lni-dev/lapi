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

import me.linusdev.data.functions.Converter;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.GuildTextChannelAbstract;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-example-guild-text-channel" target="_top">Exmaple GuildImpl Text Channel</a>
 * @see GuildTextChannelAbstract
 */
public class GuildTextChannel extends Channel<GuildTextChannel> implements GuildTextChannelAbstract {

    protected @NotNull String name;
    protected @Nullable String topic;
    protected boolean nsfw;
    protected final @Nullable Snowflake guildId;
    protected @Nullable Integer position;
    protected @NotNull PermissionOverwrites permissionOverwrites;
    protected @Nullable Snowflake parentId;
    protected int rateLimitPerUser;
    protected @Nullable Snowflake lastMessageId;
    protected @Nullable ISO8601Timestamp lastPinTimestamp;

    public GuildTextChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                            @NotNull String name, @Nullable String topic, boolean nsfw,
                            @Nullable Snowflake guildId, @Nullable Integer position, @NotNull PermissionOverwrites permissionOverwrites,
                            @Nullable Snowflake parentId, int rateLimitPerUser, @Nullable Snowflake lastMessageId,
                            @Nullable ISO8601Timestamp lastPinTimestamp) {
        super(lApi, id, type);

        this.name = name;
        this.topic = topic;
        this.nsfw = nsfw;
        this.guildId = guildId;
        this.position = position;
        this.permissionOverwrites = permissionOverwrites;
        this.parentId = parentId;
        this.rateLimitPerUser = rateLimitPerUser;
        this.lastMessageId = lastMessageId;
        this.lastPinTimestamp = lastPinTimestamp;

    }

    public GuildTextChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        Number position = (Number) data.get(POSITION_KEY);

        if (name == null) {
            throw new InvalidDataException(data, "field '" + NAME_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(NAME_KEY);
        }/* else if (guildId == null) { guildId may be null....
            throw new InvalidDataException(data, "field '" + GUILD_ID_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(GUILD_ID_KEY);
        } else if (position == -1) { position may be null...
            throw new InvalidDataException(data, "field '" + POSITION_KEY + "' missing or -1 in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(POSITION_KEY);
        }*/

        this.name = name;
        this.topic = (String) data.get(TOPIC_KEY);
        this.nsfw = (boolean) data.getOrDefaultBoth(NSFW_KEY, false);
        this.guildId = guildId;
        this.position = position == null ? null : position.intValue();
        this.permissionOverwrites = new PermissionOverwrites(data.getList(PERMISSION_OVERWRITES_KEY, new ArrayList<>()));
        this.parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
        this.rateLimitPerUser = ((Number) data.getOrDefaultBoth(RATE_LIMIT_PER_USER_KEY, 0)).intValue();
        this.lastMessageId = Snowflake.fromString((String) data.getOrDefault(LAST_MESSAGE_ID_KEY, null));
        this.lastPinTimestamp = ISO8601Timestamp.fromString((String) data.get(LAST_PIN_TIMESTAMP_KEY));

    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @Nullable String getTopic() {
        return topic;
    }

    @Override
    public boolean getNsfw() {
        return nsfw;
    }

    @Override
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }


    @Override
    public @Nullable String getGuildId() {
        if(guildId == null) return null;
        return guildId.asString();
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
    public int getRateLimitPerUser() {
        return rateLimitPerUser;
    }

    @Override
    public @Nullable Snowflake getLastMessageIdAsSnowflake() {
        return lastMessageId;
    }

    @Override
    public @Nullable ISO8601Timestamp getLastPinTimestamp() {
        return lastPinTimestamp;
    }

    @Override
    public @NotNull GuildTextChannel copy() {
        return new GuildTextChannel(lApi,
                Copyable.copy(id),
                type,
                Copyable.copy(name),
                Copyable.copy(topic),
                nsfw,
                Copyable.copy(guildId),
                position,
                Copyable.copy(permissionOverwrites),
                Copyable.copy(parentId),
                rateLimitPerUser,
                Copyable.copy(lastMessageId),
                Copyable.copy(lastPinTimestamp)
                );
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
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
    }

    @Override
    public String toString() {
        return "GuildTextChannel{" +
                "name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", nsfw=" + nsfw +
                ", guildId=" + guildId +
                ", position=" + position +
                ", permissionOverwrites=" + permissionOverwrites +
                ", parentId=" + parentId +
                ", rateLimitPerUser=" + rateLimitPerUser +
                ", lastMessageId=" + lastMessageId +
                ", lastPinTimestamp=" + lastPinTimestamp +
                '}';
    }
}
