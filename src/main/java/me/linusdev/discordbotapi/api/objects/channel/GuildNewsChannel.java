package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is basically the same as {@link GuildTextChannel}
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-example-guild-news-channel" target="_top">Exmaple Guild News Channel</a>
 * Bots can post or publish messages in this type of channel if they have the proper permissions. These are called "Announcement Channels" in the client.
 * */
public class GuildNewsChannel extends GuildTextChannel{
    public GuildNewsChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull String name, @Nullable String topic, boolean nsfw, @NotNull Snowflake guildId, int position, @NotNull PermissionOverwrites permissionOverwrites, @Nullable Snowflake parentId, int rateLimitPerUser, @Nullable Snowflake lastMessageId, @Nullable String lastPinTimestamp) {
        super(lApi, id, type, name, topic, nsfw, guildId, position, permissionOverwrites, parentId, rateLimitPerUser, lastMessageId, lastPinTimestamp);
    }

    public GuildNewsChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);
    }
}
