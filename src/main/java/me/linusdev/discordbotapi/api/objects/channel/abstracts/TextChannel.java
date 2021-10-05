package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import me.linusdev.discordbotapi.api.objects.Snowflake;
import org.jetbrains.annotations.Nullable;

public interface TextChannel {

    /**
     *the id of the last message snowflake id sent in this channel (may not point to an existing or valid message)
     * todo may be null if no msg was sent at all?
     */
    @Nullable
    Snowflake getLastMessageIdAsSnowflake();

    /**
     *the id of the last message id sent in this channel (may not point to an existing or valid message)
     * todo may be null if no msg was sent at all?
     */
    @Nullable
    default String getLastMessageId(){
        Snowflake snowflake = getLastMessageIdAsSnowflake();
        if(snowflake == null) return null;
        return snowflake.asString();
    }

    /**
     * when the last pinned message was pinned. This may be {@code null} in events such as GUILD_CREATE when a message is not pinned.
     * todo link event and check output
     */
    @Nullable
    String getLastPinTimestamp();
}
