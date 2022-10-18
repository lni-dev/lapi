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

package me.linusdev.lapi.api.objects.channel.thread;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.nchannel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.nchannel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.GuildChannel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.nchannel.ChannelType;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildPublicThread extends Channel<GuildPublicThread> implements Thread<GuildPublicThread>, GuildChannel {

    protected @NotNull String name;
    protected @NotNull Snowflake guildId;
    protected @NotNull Snowflake parentId;
    protected int rateLimitPerUser;
    protected @Nullable Snowflake lastMessageId;
    protected @Nullable ISO8601Timestamp lastPinTimestamp;
    protected @NotNull Snowflake ownerId;
    protected @NotNull ThreadMetadata threadMetadata;
    protected int messageCount;
    protected int memberCount;
    protected @Nullable ThreadMember member;
    protected @Nullable Integer defaultAutoArchiveDuration;
    protected @Nullable String permissionsAsString;

    public GuildPublicThread(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull String name, @NotNull Snowflake guildId, @NotNull Snowflake parentId, int rateLimitPerUser,
                             @Nullable Snowflake lastMessageId, @Nullable ISO8601Timestamp lastPinTimestamp, @NotNull Snowflake ownerId, @NotNull ThreadMetadata threadMetadata, int messageCount, int memberCount,
                             @Nullable ThreadMember member, @Nullable Integer defaultAutoArchiveDuration, @Nullable String permissionsAsString) {
        super(lApi, id, type);

        this.name = name;
        this.guildId = guildId;
        this.parentId = parentId;
        this.rateLimitPerUser = rateLimitPerUser;
        this.lastMessageId = lastMessageId;
        this.lastPinTimestamp = lastPinTimestamp;
        this.ownerId = ownerId;
        this.threadMetadata = threadMetadata;
        this.messageCount = messageCount;
        this.memberCount = memberCount;
        this.member = member;
        this.defaultAutoArchiveDuration = defaultAutoArchiveDuration;
        this.permissionsAsString = permissionsAsString;
    }

    public GuildPublicThread(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        Snowflake parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
        Snowflake ownerId = Snowflake.fromString((String) data.get(OWNER_ID_KEY));
        SOData threadMetadataData = (SOData) data.get(THREAD_METADATA_KEY);
        SOData member = (SOData) data.get(MEMBER_KEY);
        Integer defaultAutoArchiveDuration = (Integer) data.get(DEFAULT_AUTO_ARCHIVE_DURATION_KEY);
        String permissionsAsString = (String) data.get(PERMISSIONS_KEY);

        if (name == null) {
            throw new InvalidDataException(data, "field '" + NAME_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(NAME_KEY);
        } else if (guildId == null) {
            throw new InvalidDataException(data, "field '" + GUILD_ID_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(GUILD_ID_KEY);
        }else if(parentId == null){
            throw new InvalidDataException(data, "field '" + PARENT_ID_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(PARENT_ID_KEY);
        }else if(ownerId == null){
            throw new InvalidDataException(data, "field '" + OWNER_ID_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(OWNER_ID_KEY);
        }else if(threadMetadataData == null){
            throw new InvalidDataException(data, "field '" + THREAD_METADATA_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(THREAD_METADATA_KEY);
        }

        this.name = name;
        this.guildId = guildId;
        this.parentId = parentId;
        this.rateLimitPerUser = ((Number) data.getOrDefaultBoth(RATE_LIMIT_PER_USER_KEY, 0)).intValue();
        this.lastMessageId = Snowflake.fromString((String) data.getOrDefault(LAST_MESSAGE_ID_KEY, null));
        this.lastPinTimestamp = ISO8601Timestamp.fromString((String) data.get(LAST_PIN_TIMESTAMP_KEY));
        this.ownerId = ownerId;
        this.threadMetadata = new ThreadMetadata(threadMetadataData);
        this.memberCount = ((Number) data.getOrDefaultBoth(MEMBER_COUNT_KEY, 0)).intValue();
        this.messageCount = ((Number) data.getOrDefaultBoth(MESSAGE_COUNT_KEY, 0)).intValue();
        this.member = ThreadMember.fromData(member);
        this.defaultAutoArchiveDuration = defaultAutoArchiveDuration;
        this.permissionsAsString = permissionsAsString;

    }

    @Override
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
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
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Snowflake getParentIdAsSnowflake() {
        return parentId;
    }

    @Override
    public @NotNull String getParentId() {
        return Thread.super.getParentId();
    }

    @Override
    public @NotNull Snowflake getOwnerIdAsSnowflake() {
        return ownerId;
    }

    @Override
    public @NotNull ThreadMetadata getThreadMetadata() {
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
    public @Nullable String getPermissionsAsString() {
        return permissionsAsString;
    }

    @Override
    public int getMessageCount() {
        return messageCount;
    }

    @Override
    public int getMemberCount() {
        return memberCount;
    }

    @Override
    public int getRateLimitPerUser() {
        return rateLimitPerUser;
    }

    @Override
    public Integer getPosition() {
        throw new UnsupportedOperationException("Threads do not have a Position!");
    }

    @Override
    public @NotNull PermissionOverwrites getPermissionOverwrites() {
        throw new UnsupportedOperationException("Threads do not have permission overwrites!");
    }

    @Override
    public boolean getNsfw() {
        throw new UnsupportedOperationException("Threads do not have an nsfw tag. It's always the same as the parent channel!");
    }

    @Override
    public @NotNull GuildPublicThread copy() {
        return new GuildPublicThread(lApi,
                Copyable.copy(id),
                type,
                Copyable.copy(name),
                Copyable.copy(guildId),
                Copyable.copy(parentId),
                rateLimitPerUser,
                Copyable.copy(lastMessageId),
                Copyable.copy(lastPinTimestamp),
                Copyable.copy(ownerId),
                Copyable.copy(threadMetadata),
                messageCount,
                memberCount,
                Copyable.copy(member),
                defaultAutoArchiveDuration,
                Copyable.copy(permissionsAsString)
        );
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        super.updateSelfByData(data);

        data.processIfContained(NAME_KEY, (String str) -> this.name = str);
        //guildId should never change

        data.processIfContained(PARENT_ID_KEY, (String str) -> this.parentId = Snowflake.fromString(str));
        data.processIfContained(RATE_LIMIT_PER_USER_KEY, (Number num) -> {if(num != null)this.rateLimitPerUser = num.intValue();});
        data.processIfContained(LAST_MESSAGE_ID_KEY, (String str) -> this.lastMessageId = Snowflake.fromString(str));
        data.processIfContained(LAST_PIN_TIMESTAMP_KEY, (String str) -> this.lastPinTimestamp = ISO8601Timestamp.fromString(str));
        data.processIfContained(OWNER_ID_KEY, (String str) -> this.ownerId = Snowflake.fromString(str));

        SOData d = (SOData) data.get(THREAD_METADATA_KEY);
        if (d != null) {
            this.threadMetadata = new ThreadMetadata(d);
        }

        data.processIfContained(MESSAGE_COUNT_KEY, (Number num) -> {if(num != null)this.messageCount = num.intValue();});
        data.processIfContained(MEMBER_COUNT_KEY, (Number num) -> {if(num != null)this.memberCount = num.intValue();});

        d = (SOData) data.get(MEMBER_KEY);
        if (d != null) {
            this.member = ThreadMember.fromData(d);
        }

        data.processIfContained(DEFAULT_AUTO_ARCHIVE_DURATION_KEY, (Number num) -> {
            if(num != null)this.defaultAutoArchiveDuration = num.intValue();
            else this.defaultAutoArchiveDuration = null;
        });

        data.processIfContained(PERMISSIONS_KEY, (String str) -> this.permissionsAsString = str);
    }

    @Override
    public @Nullable ThreadMember updateMember(@Nullable ThreadMember newMember) {
        ThreadMember old = member;
        member = newMember;
        return old;
    }

    @Override
    public String toString() {
        return "GuildPublicThread: " + name;
    }
}
