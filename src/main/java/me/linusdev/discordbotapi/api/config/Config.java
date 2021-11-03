package me.linusdev.discordbotapi.api.config;

public class Config {

    /**
     * retrieves and saves the voice regions on startup
     */
    public final static long LOAD_VOICE_REGIONS_ON_STARTUP = 0x1L;

    private final long flags;

    public Config(long flags){
        this.flags = flags;
    }

    /**
     *
     * @param flag to check, can also be more than one flag
     * @return true if all bits in flag are also set int {@link #flags}
     */
    public boolean isFlagSet(long flag){
        return (flags & flag) == flag;
    }

}
