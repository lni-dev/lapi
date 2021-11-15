package me.linusdev.discordbotapi.api.objects.message;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Nullable;

public class MessageReference implements Datable {

    //TODO

    private @Nullable Snowflake messageId;
    private @Nullable Snowflake channelId;
    private @Nullable Snowflake guildId;
    private boolean fallIfNotExists;

    public static @Nullable MessageReference fromData(@Nullable Data data){
        if(data == null) return null;
        //TODO
        return null;
    }

    /**
     * id as {@link Snowflake} of the originating message
     */
    public @Nullable Snowflake getMessageIdAsSnowflake() {
        return messageId;
    }

    /**
     * id as {@link String} of the originating message
     */
    public String getMessageId() {
        return messageId == null ? null : messageId.asString();
    }

    /**
     * id as {@link Snowflake} of the originating message's channel
     */
    public @Nullable Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * id as {@link String} of the originating message's channel
     */
    public String getChannelId() {
        return channelId == null ? null : channelId.asString();
    }

    /**
     * id as {@link Snowflake} of the originating message's guild
     */
    public Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * id as {@link String} of the originating message's guild
     */
    public String getGuildId(){
        return guildId == null ? null : guildId.asString();
    }

    /**
     * when sending, whether to error if the referenced message doesn't exist instead of sending as a normal (non-reply) message, default {@code true}
     */
    public boolean doesFallIfNotExists(){
        return fallIfNotExists;
    }

    @Override
    public Data getData() {
        Data data = new Data(0);
        return data;
    }
}
