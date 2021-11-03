package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import me.linusdev.discordbotapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuildChannel {

    /**
     * sorting position of the channel
     * todo give more information
     */
    int getPosition();

    /**
     * explicit permission overwrites for members and roles
     */
    @NotNull
    PermissionOverwrites getPermissionOverwrites();

    /**
     * the name of the channel (1-100 characters)
     */
    @NotNull
    String getName();

    /**
     * whether the channel is nsfw
     */
    boolean getNsfw();

    /**
     * for guild channels: id of the parent category for a channel
     * (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
     */
    @Nullable
    Snowflake getParentIdAsSnowflake();

    /**
     * for guild channels: id of the parent category for a channel
     * (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
     */
    @Nullable default String getParentId(){
        if(getParentIdAsSnowflake() == null) return null;
        return getParentIdAsSnowflake().asString();
    }

    /**
     * the guild id
     */
    @NotNull default String getGuildId(){
        return getGuildIdAsSnowflake().asString();
    }

    /**
     * the guild id snowflake or {@code null} if this is not a guild channel
     */
    @NotNull Snowflake getGuildIdAsSnowflake();
}
