package me.linusdev.discordbotapi.api.objects.stage;

import me.linusdev.data.SimpleDatable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/stage-instance#stage-instance-object-privacy-level" target="_top">Privacy Level</a>
 */
public enum PrivacyLevel implements SimpleDatable {
    /**
     * LApi specific
     */
    UNKNOWN(0),

    /**
     * The Stage instance is visible publicly, such as on Stage Discovery.
     */
    PUBLIC(1),

    /**
     * The Stage instance is visible to only guild members.
     */
    GUILD_ONLY(2),
    ;

    private final int value;

    PrivacyLevel(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link PrivacyLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static PrivacyLevel fromValue(int value){
        for(PrivacyLevel level : PrivacyLevel.values()){
            if(level.value == value) return level;
        }
        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
