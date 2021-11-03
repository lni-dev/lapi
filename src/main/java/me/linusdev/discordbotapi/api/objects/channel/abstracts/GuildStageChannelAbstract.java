package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import org.jetbrains.annotations.Nullable;

public interface GuildStageChannelAbstract extends GuildVoiceChannelAbstract{

    /**
     * the channel topic (1-120 characters)
     * I have not found any documentation about this. This information is purely based on
     * my testing in Discord
     */
    @Nullable
    String getTopic();
}
