package me.linusdev.discordbotapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level" target="_top">Guild NSFW Level</a>
 */
public enum GuildNsfwLevel implements SimpleDatable {

    /**
     * LApi Specific
     */
    UNKNOWN(-1),

    DEFAULT(0),
    EXPLICIT(1),
    SAFE(2),
    AGE_RESTRICTED(3),
    ;

    private final int value;

    GuildNsfwLevel(int value) {
        this.value = value;
    }

    /**
     *
     * @param value int value
     * @return {@link GuildNsfwLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull GuildNsfwLevel fromValue(int value){
        for(GuildNsfwLevel level : GuildNsfwLevel.values()){
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
