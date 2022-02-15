package me.linusdev.discordbotapi.api.config;

import me.linusdev.data.Data;
import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("PointlessBitwiseExpression")
public enum ConfigFlag implements SimpleDatable {
    /**
     * This flag has no effects
     */
    NOTHING(0),

    /**
     * <p>
     *     retrieves and caches all voice regions when {@link me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl LApi} starts.
     * </p>
     * @see me.linusdev.discordbotapi.api.VoiceRegionManager VoiceRegionManager
     */
    CACHE_VOICE_REGIONS(1 << 0),

    ENABLE_GATEWAY(1 << 1),

    /**
     * <p>
     *     Caches all {@link me.linusdev.discordbotapi.api.objects.role.Role roles} retrieved by the
     *     {@link me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     *
     * @see me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild UpdatableGuild
     * @see me.linusdev.discordbotapi.api.manager.guild.role.RoleManager RoleManager
     */
    CACHE_ROLES(1 << 2),

    /**
     * <p>
     *     Copies {@link me.linusdev.discordbotapi.api.objects.role.Role role} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     *
     * @see me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild UpdatableGuild
     * @see me.linusdev.discordbotapi.api.manager.guild.role.RoleManager RoleManager
     */
    COPY_ROLE_ON_UPDATE_EVENT(1 << 3),

    ;

    private final long value;

    ConfigFlag(long value) {
        this.value = value;
    }

    public static long fromData(@NotNull Data data){
        long flags = 0;

        for(ConfigFlag flag : ConfigFlag.values()){
            Boolean set = (Boolean) data.get(flag.name());

            if(set == null || !set) continue;
            flags = flags | flag.value;
        }

        return flags;
    }

    /**
     *
     * @param flags long with set bits
     * @return {@link Data} representing given flags
     */
    public static @NotNull Data toData(long flags){
        ConfigFlag[] values = ConfigFlag.values();
        Data data = new Data(values.length);

        for(ConfigFlag flag : values) {
            if(flag == NOTHING) continue;
            data.add(flag.toString(), flag.isSet(flags));
        }

        return data;
    }

    /**
     *
     * @param flags long with set bits
     * @return {@code true} if this flag is set in given long, {@code false} otherwise
     */
    public boolean isSet(long flags){
        return (flags & value) != 0;
    }

    /**
     * Sets the bit for this flag. If it is already set, this will do nothing
     * @param flags long
     * @return new flags long
     */
    public long  set(long flags){
        return flags | value;
    }

    /**
     * Unsets the bit for this flag. If it is not set, this will do nothing
     * @param flags long
     * @return new flags long
     */
    public long  remove(long flags){
        return flags & (~value);
    }

    @Override
    public Object simplify() {
        return toString();
    }

    @Override
    public String toString() {
        return name();
    }
}
