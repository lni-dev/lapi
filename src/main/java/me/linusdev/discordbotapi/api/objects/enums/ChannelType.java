package me.linusdev.discordbotapi.api.objects.enums;

import me.linusdev.data.SimpleDatable;
import me.linusdev.discordbotapi.api.objects.permission.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * This contains the different types of channels.
 * <a href="https://discord.com/developers/docs/resources/channel#channel-object-channel-types" target="_top">Representing discord documentation</a>
 */
public enum ChannelType implements SimpleDatable {

    /**
     * This is LApi specific, in case discord adds another channel type
     *
     * It's id is -2, cause -1 is sometimes used to check if the type field is missing in a {@link me.linusdev.data.Data}
     */
    UNKNOWN(-2, "Unknown Channel", SimpleChannelType.UNKNOWN),

    /**
     * a text channel within a server
     */
    GUILD_TEXT(0, "GuildImpl Text Channel", SimpleChannelType.TEXT),

    /**
     * a direct message between users
     */
    DM(1, "DM Channel", SimpleChannelType.TEXT),

    /**
     * a voice channel within a server
     */
    GUILD_VOICE(2, "GuildImpl Voice Channel", SimpleChannelType.VOICE),

    /**
     * a direct message between multiple users
     */
    GROUP_DM(3, "Group DM Channel", SimpleChannelType.TEXT),

    /**
     * an organizational
     * <a href="https://support.discord.com/hc/en-us/articles/115001580171-Channel-Categories-101" target="_top">
     * category
     * </a>
     * that contains up to 50 channels
     */
    GUILD_CATEGORY(4, "Category Channel", SimpleChannelType.CATEGORY),

    /**
     * a channel that
     * <a href="https://support.discord.com/hc/en-us/articles/360032008192" target="_top">
     * users can follow and crosspost into their own server
     * </a>
     */
    GUILD_NEWS(5, "GuildImpl News Channel", SimpleChannelType.TEXT),

    /**
     * a channel in which game developers can
     * <a href="https://discord.com/developers/docs/game-and-server-management/special-channels" target="_top">
     * sell their game on Discord
     * </a>
     */
    GUILD_STORE(6, "GuildImpl Store Channel", SimpleChannelType.TEXT),

    /**
     * a temporary sub-channel within a GUILD_NEWS channel
     */
    GUILD_NEWS_THREAD(10, "GuildImpl News Thread", SimpleChannelType.THREAD),

    /**
     * a temporary sub-channel within a GUILD_TEXT channel
     */
    GUILD_PUBLIC_THREAD(11, "GuildImpl Public Thread", SimpleChannelType.THREAD),

    /**
     * a temporary sub-channel within a {@link ChannelType#GUILD_TEXT GUILD_TEXT} channel
     * that is only viewable by those invited and those with the
     * {@link Permission#MANAGE_THREADS MANAGE_THREADS} permission
     */
    GUILD_PRIVATE_THREAD(12, "GuildImpl Private Chat", SimpleChannelType.THREAD),

    /**
     * a voice channel for
     * <a href="https://support.discord.com/hc/en-us/articles/1500005513722" target="_top">
     * hosting events with an audience
     * </a>
     */
    GUILD_STAGE_VOICE(13, "GuildImpl Stage Voice Channel", SimpleChannelType.STAGE),
    ;

    private final int id;
    private final String string;
    private final SimpleChannelType simpleChannelType;

    ChannelType(int id, String string, SimpleChannelType simpleChannelType) {
        this.id = id;
        this.string = string;
        this.simpleChannelType = simpleChannelType;
    }

    public int getId() {
        return id;
    }

    /**
     * @see SimpleChannelType
     */
    public SimpleChannelType getSimpleChannelType() {
        return simpleChannelType;
    }

    /**
     * Converts from id to {@link ChannelType}
     *
     * @param id of the wanted {@link ChannelType}.
     * @return {@link ChannelType} with given id or {@link #UNKNOWN} if no such {@link ChannelType} exists
     */
    public static @NotNull ChannelType fromId(int id) {
        for (ChannelType type : ChannelType.values())
            if (type.getId() == id) return type;
        return UNKNOWN;
    }

    /**
     * @return a readable String for example "GuildImpl Text Channel"
     */
    public String asString() {
        return string;
    }

    /**
     * @return the name of this enum constant, as contained in the declaration.
     */
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Object simplify() {
        return id;
    }
}
