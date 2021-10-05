package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.PermissionOverwrite;
import me.linusdev.discordbotapi.api.objects.PermissionOverwrites;
import me.linusdev.discordbotapi.api.objects.Snowflake;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.GuildChannel;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ChannelCategory extends Channel implements GuildChannel {

    private @NotNull String name;
    private boolean nsfw;
    private @NotNull Snowflake guildId;
    private int position;
    private @NotNull PermissionOverwrites permissionOverwrites;
    private @Nullable Snowflake parentId;

    public ChannelCategory(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                            @NotNull String name, boolean nsfw,
                            @NotNull Snowflake guildId, int position, @NotNull PermissionOverwrites permissionOverwrites,
                            @Nullable Snowflake parentId) {
        super(lApi, id, type);

        this.name = name;
        this.nsfw = nsfw;
        this.guildId = guildId;
        this.position = position;
        this.permissionOverwrites = permissionOverwrites;
        this.parentId = parentId;

    }

    public ChannelCategory(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        int position = ((Number) data.getOrDefault(POSITION_KEY, -1)).intValue(); //todo can position be missing?

        if (name == null) {
            throw new InvalidDataException("field '" + NAME_KEY + "' missing or null in ChannelCategory with id:" + getId());
        } else if (guildId == null) {
            throw new InvalidDataException("field '" + GUILD_ID_KEY + "' missing or null in ChannelCategory with id:" + getId());
        } else if (position == -1) {
            throw new InvalidDataException("field '" + POSITION_KEY + "' missing or -1 in ChannelCategory with id:" + getId());
        }

        this.name = name;
        this.nsfw = (boolean) data.getOrDefault(NSFW_KEY, false);
        this.guildId = guildId;
        this.position = position;
        this.permissionOverwrites = new PermissionOverwrites((ArrayList<Data>) data.getOrDefault(PERMISSION_OVERWRITES_KEY, new ArrayList<>()));
        this.parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
    }

    @Override
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
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
}
