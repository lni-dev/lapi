package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import me.linusdev.discordbotapi.api.objects.enums.VideoQuality;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VoiceChannel {

    /**
     * the bitrate (in bits) of the voice channel
     */
    int getBitRate();

    /**
     * the user limit of the voice channel
     *
     * Possibly 0, this means there is no limit set
     */
    int getUserLimit();

    /**
     * <a href="https://discord.com/developers/docs/resources/voice#voice-region-object" target="_top">voice region</a> id for the voice channel, automatic when set to null
     */
    @Nullable
    String getRTCRegion();

    /**
     * the camera {@link VideoQuality} of the voice channel, 1 when not present
     */
    @NotNull
    VideoQuality getVideoQualityMode();
}
