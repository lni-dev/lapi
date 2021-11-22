package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.GuildTextChannelAbstract;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-example-guild-text-channel" target="_top">Exmaple Guild Text Channel</a>
 * @see GuildTextChannelAbstract
 */
public class GuildTextChannel extends Channel implements GuildTextChannelAbstract {

    private @NotNull String name;
    private @Nullable String topic;
    private boolean nsfw;
    private @Nullable Snowflake guildId;
    private @Nullable Integer position;
    private @NotNull PermissionOverwrites permissionOverwrites;
    private @Nullable Snowflake parentId;
    private int rateLimitPerUser;
    private @Nullable Snowflake lastMessageId;
    private @Nullable String lastPinTimestamp;

    public GuildTextChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                            @NotNull String name, @Nullable String topic, boolean nsfw,
                            @Nullable Snowflake guildId, @Nullable Integer position, @NotNull PermissionOverwrites permissionOverwrites,
                            @Nullable Snowflake parentId, int rateLimitPerUser, @Nullable Snowflake lastMessageId,
                            @Nullable String lastPinTimestamp) {
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

    public GuildTextChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
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
        this.nsfw = (boolean) data.getOrDefault(NSFW_KEY, false);
        this.guildId = guildId;
        this.position = position == null ? null : position.intValue();
        this.permissionOverwrites = new PermissionOverwrites((ArrayList<Data>) data.getOrDefault(PERMISSION_OVERWRITES_KEY, new ArrayList<>()));
        this.parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
        this.rateLimitPerUser = ((Number) data.getOrDefault(RATE_LIMIT_PER_USER_KEY, 0)).intValue();
        this.lastMessageId = Snowflake.fromString((String) data.getOrDefault(LAST_MESSAGE_ID_KEY, null));
        this.lastPinTimestamp = (String) data.get(LAST_PIN_TIMESTAMP_KEY);

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
    public int getPosition() {
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
    public @Nullable String getLastPinTimestamp() {
        return lastPinTimestamp;
    }
}
