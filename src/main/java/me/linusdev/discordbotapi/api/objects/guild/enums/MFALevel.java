package me.linusdev.discordbotapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-mfa-level" target="_top">MFA Level</a>
 */
public enum MFALevel implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * guild has no MFA/2FA requirement for moderation actions
     */
    NONE(0),

    /**
     * guild has a 2FA requirement for moderation actions
     */
    ELEVATED(1),

    ;

    private final int integer;

    MFALevel(int integer) {
        this.integer = integer;
    }

    /**
     *
     * @param value int
     * @return {@link MFALevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull MFALevel fromValue(int value){
        for(MFALevel level : MFALevel.values()){
            if(level.integer == value) return level;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return integer;
    }

    @Override
    public Object simplify() {
        return integer;
    }
}
