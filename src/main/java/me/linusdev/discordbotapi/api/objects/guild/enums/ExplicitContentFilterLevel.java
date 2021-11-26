package me.linusdev.discordbotapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-explicit-content-filter-level" target="_top">Explicit Content Filter Level</a>
 */
public enum ExplicitContentFilterLevel implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * media content will not be scanned
     */
    DISABLED(0),

    /**
     * media content sent by members without roles will be scanned
     */
    MEMBERS_WITHOUT_ROLES(1),

    /**
     * media content sent by all members will be scanned
     */
    ALL_MEMBERS(2),
    ;

    private final int integer;

    ExplicitContentFilterLevel(int integer) {
        this.integer = integer;
    }

    /**
     *
     * @param value int
     * @return {@link ExplicitContentFilterLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static ExplicitContentFilterLevel fromValue(int value){
        for(ExplicitContentFilterLevel level : ExplicitContentFilterLevel.values()){
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
