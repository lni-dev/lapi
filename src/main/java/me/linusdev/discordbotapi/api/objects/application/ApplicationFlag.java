package me.linusdev.discordbotapi.api.objects.application;

import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/application#application-object-application-flags" target="_top">Application Flags</a>
 */
public enum ApplicationFlag{
    GATEWAY_PRESENCE                    (1 << 12),
    GATEWAY_PRESENCE_LIMITED            (1 << 13),
    GATEWAY_GUILD_MEMBERS               (1 << 14),
    GATEWAY_GUILD_MEMBERS_LIMITED       (1 << 15),
    VERIFICATION_PENDING_GUILD_LIMIT    (1 << 16),
    EMBEDDED                            (1 << 17),
    GATEWAY_MESSAGE_CONTENT             (1 << 18),
    GATEWAY_MESSAGE_CONTENT_LIMITED     (1 << 19),
    ;

    private final int value;

    ApplicationFlag(int value){
        this.value = value;
    }

    /**
     *
     * @param flags int with set flags
     * @return {@link ApplicationFlag}[] with corresponding {@link ApplicationFlag application flags}
     */
    public static @NotNull ApplicationFlag[] getFlagsFromInteger(int flags){
        ApplicationFlag[] flagsArray = new ApplicationFlag[Integer.bitCount(flags)];

        int i = 0;
        for(ApplicationFlag flag : ApplicationFlag.values()){
            if((flags & flag.getValue()) == flag.getValue())
                flagsArray[i++] = flag;
        }

        return flagsArray;
    }

    /**
     * value of this flag as {@link Integer} with 1 bit set
     */
    public int getValue() {
        return value;
    }
}
