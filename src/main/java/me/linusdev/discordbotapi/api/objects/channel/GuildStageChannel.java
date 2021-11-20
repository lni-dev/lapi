package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.GuildStageChannelAbstract;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import me.linusdev.discordbotapi.api.objects.enums.VideoQuality;
import me.linusdev.discordbotapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildStageChannel extends GuildVoiceChannel implements GuildStageChannelAbstract {

    private @Nullable String topic;

    public GuildStageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull String name, boolean nsfw, @NotNull Snowflake guildId, int position, @NotNull PermissionOverwrites permissionOverwrites, @Nullable Snowflake parentId, int bitRate, int userLimit, @Nullable String rtcRegion, @NotNull VideoQuality videoQualityMode, @Nullable String topic) {
        super(lApi, id, type, name, nsfw, guildId, position, permissionOverwrites, parentId, bitRate, userLimit, rtcRegion, videoQualityMode);
        this.topic = topic;
    }

    public GuildStageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);
        this.topic = (String) data.get(Channel.TOPIC_KEY, null);
    }

    @Override
    public @Nullable String getTopic() {
        return topic;
    }
}
