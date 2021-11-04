package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import me.linusdev.discordbotapi.api.objects.enums.VideoQuality;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.GuildVoiceChannelAbstract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GuildVoiceChannel extends Channel implements GuildVoiceChannelAbstract {

    private @NotNull String name;
    private boolean nsfw;
    private @NotNull Snowflake guildId;
    private int position;
    private @NotNull PermissionOverwrites permissionOverwrites;
    private @Nullable Snowflake parentId;
    private int bitRate;
    private int userLimit;
    private @Nullable String rtcRegion;
    private @NotNull VideoQuality videoQualityMode;

    public GuildVoiceChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                             @NotNull String name, boolean nsfw, @NotNull Snowflake guildId,
                             int position, @NotNull PermissionOverwrites permissionOverwrites, @Nullable Snowflake parentId,
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

    public GuildVoiceChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);

        String name = (String) data.get(NAME_KEY);
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        int position = ((Number) data.getOrDefault(POSITION_KEY, -1)).intValue(); //todo can position be missing?

        if (name == null) {
            throw new InvalidDataException(data, "field '" + NAME_KEY + "' missing or null in GuildVoiceChannel with id:" + getId()).addMissingFields(NAME_KEY);
        } else if (guildId == null) {
            throw new InvalidDataException(data, "field '" + GUILD_ID_KEY + "' missing or null in GuildVoiceChannel with id:" + getId()).addMissingFields(GUILD_ID_KEY);
        } else if (position == -1) {
            throw new InvalidDataException(data, "field '" + POSITION_KEY + "' missing or -1 in GuildVoiceChannel with id:" + getId()).addMissingFields(POSITION_KEY);
        }

        this.name = name;
        this.nsfw = (boolean) data.getOrDefault(NSFW_KEY, false);
        this.guildId = guildId;
        this.position = position;
        this.permissionOverwrites = new PermissionOverwrites((ArrayList<Data>) data.getOrDefault(PERMISSION_OVERWRITES_KEY, new ArrayList<>()));
        this.parentId = Snowflake.fromString((String) data.get(PARENT_ID_KEY));
        this.bitRate = ((Number) data.getOrDefault(BITRATE_KEY, -1)).intValue();
        this.userLimit = ((Number) data.getOrDefault(USER_LIMIT_KEY, 0)).intValue();
        this.rtcRegion = (String) data.getOrDefault(RTC_REGION_KEY, null);
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
}
