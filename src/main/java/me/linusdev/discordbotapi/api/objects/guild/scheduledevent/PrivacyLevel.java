package me.linusdev.discordbotapi.api.objects.guild.scheduledevent;

import me.linusdev.data.SimpleDatable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-privacy-level" target="_top">GuildImpl Scheduled Event Privacy Level</a>
 */
public enum PrivacyLevel implements SimpleDatable {
    /**
     * LApi specific
     */
    UNKNOWN(0),

    /**
     * the scheduled event is only accessible to guild members
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
