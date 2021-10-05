package me.linusdev.discordbotapi.api.objects.enums;

import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-video-quality-modes" target="_top">Video Quality Modes</a>
 */
public enum VideoQuality {

    /**
     * This is LApi specific, in case discord adds another quality type
     */
    UNKNOWN(-2),

    /**
     * Discord chooses the quality for optimal performance
     */
    AUTO(1),

    /**
     * 720p
     */
    FULL(2),
    ;

    private final int id;

    VideoQuality(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * @return the VideoQuality representing the id. If Id is {@code null}, {@link #AUTO} is returned. This is the Default
     */
    public static VideoQuality fromId(@Nullable Number id) {
        if(id == null) return AUTO;
        for (VideoQuality type : VideoQuality.values())
            if (type.getId() == id.intValue()) return type;
        return UNKNOWN;
    }
}
