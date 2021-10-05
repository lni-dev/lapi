package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.PermissionOverwrites;
import me.linusdev.discordbotapi.api.objects.Snowflake;
import me.linusdev.discordbotapi.api.objects.ThreadMetadata;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.GuildChannel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildNewsThread extends Channel implements Thread, GuildChannel {

    private @NotNull String name;
    private @NotNull Snowflake guildId;
    private @NotNull Snowflake parentId;
    private int rateLimitPerUser;
    private @Nullable Snowflake lastMessageId;
    private @Nullable String lastPinTimestamp;
    private @NotNull Snowflake ownerId;
    private @NotNull ThreadMetadata threadMetadata;
    private int messageCount;
    private int memberCount;

    public GuildNewsThread(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull String name, @NotNull Snowflake guildId, @NotNull Snowflake parentId, int rateLimitPerUser,
                             @Nullable Snowflake lastMessageId, @Nullable String lastPinTimestamp, @NotNull Snowflake ownerId, @NotNull ThreadMetadata threadMetadata, int messageCount, int memberCount) {
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
    }

    public GuildNewsThread(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        Snowflake parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
        Snowflake ownerId = Snowflake.fromString((String) data.get(OWNER_ID_KEY));
        Data threadMetadataData = (Data) data.get(THREAD_METADATA_KEY);

        if (name == null) {
            throw new InvalidDataException("field '" + NAME_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId());
        } else if (guildId == null) {
            throw new InvalidDataException("field '" + GUILD_ID_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId());
        }else if(parentId == null){
            throw new InvalidDataException("field '" + PARENT_ID_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId());
        }else if(ownerId == null){
            throw new InvalidDataException("field '" + OWNER_ID_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId());
        }else if(threadMetadataData == null){
            throw new InvalidDataException("field '" + THREAD_METADATA_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId());
        }

        this.name = name;
        this.guildId = guildId;
        this.parentId = parentId;
        this.rateLimitPerUser = ((Number) data.getOrDefault(RATE_LIMIT_PER_USER_KEY, 0)).intValue();
        this.lastMessageId = Snowflake.fromString((String) data.getOrDefault(LAST_MESSAGE_ID_KEY, null));
        this.lastPinTimestamp = (String) data.get(LAST_PIN_TIMESTAMP_KEY);
        this.ownerId = ownerId;
        this.threadMetadata = new ThreadMetadata(threadMetadataData);
        this.memberCount = ((Number) data.getOrDefault(MEMBER_COUNT_KEY, 0)).intValue();
        this.messageCount = ((Number) data.getOrDefault(MESSAGE_COUNT_KEY, 0)).intValue();

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
    public @Nullable String getLastPinTimestamp() {
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
    public int getPosition() {
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
}
