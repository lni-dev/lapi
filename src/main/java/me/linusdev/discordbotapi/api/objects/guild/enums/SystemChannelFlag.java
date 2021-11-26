package me.linusdev.discordbotapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags" target="_top">System Channel Flags</a>
 */
public enum SystemChannelFlag implements SimpleDatable {

    /**
     * Suppress member join notifications
     */
    SUPPRESS_JOIN_NOTIFICATIONS(1 << 0),

    /**
     * Suppress server boost notifications
     */
    SUPPRESS_PREMIUM_SUBSCRIPTIONS(1 << 1),

    /**
     * Suppress server setup tips
     */
    SUPPRESS_GUILD_REMINDER_NOTIFICATIONS(1 << 2),

    /**
     * Hide member join sticker reply buttons
     */
    SUPPRESS_JOIN_NOTIFICATION_REPLIES(1 << 3),
    ;

    private final int value;

    SystemChannelFlag(int value) {
        this.value = value;
    }

    /**
     *
     * @param flags int with set bits
     * @return {@link SystemChannelFlag} array containing all flags set in given int
     */
    public static @NotNull SystemChannelFlag[] fromValue(int flags){
        SystemChannelFlag[] flagArray = new SystemChannelFlag[Integer.bitCount(flags)];

        int i = 0;
        for(SystemChannelFlag flag : SystemChannelFlag.values()){
            if(flag.isSet(flags)) {
                flagArray[i++] = flag;
            }
        }

        return flagArray;
    }

    /**
     *
     * @param flags int with set btis
     * @return {@code true} if this flag is set in flags
     */
    public boolean isSet(int flags){
        return (flags & value) == value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
