package me.linusdev.discordbotapi.api.objects.snowflake;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.DISCORD_EPOCH;

/**
 *
 * @see <a href="https://discord.com/developers/docs/reference#snowflakes" target="_top">Snowflakes<a/>
 */
public class Snowflake implements SimpleDatable {
    private final @NotNull String string;
    private Long asLong = null;
    private Long timestamp = null;
    private Integer internalWorkerID = null;
    private Integer internalProcessID = null;
    private Integer increment = null;

    private Snowflake(@NotNull String snowflake){
        this.string = snowflake;
    }

    /**
     * Creates a new Snowflake from given id-string
     *
     * @param snowflake the id-string
     * @return The Snowflake matching the given id or {@code null} if the input string was {@code null}
     */
    public static Snowflake fromString(@Nullable String snowflake){
        if(snowflake == null) return null;
        else  return new Snowflake(snowflake);
    }

    /**
     * It's Recommended to use this instead of {@link #asLong()} since LApi mostly works with String ids.
     * @return this snowflake id as string
     */
    public @NotNull String asString(){
        return string;
    }

    /**
     * @return this snowflake id as long
     */
    public @NotNull Long asLong(){
        if(asLong == null) asLong = Long.parseLong(string);
        return asLong;
    }

    /**
     *
     * @return milliseconds since 1. january 1970
     */
    public @NotNull Long getTimestamp() {
        if(timestamp == null) timestamp = (asLong() >> 22) + DISCORD_EPOCH;
        return timestamp;
    }

    /**
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Snowflake Structure<a/>
     */
    public @NotNull Integer getInternalWorkerID() {
        if(internalWorkerID == null) internalWorkerID = (int) ((asLong() & 0x3E0000) >> 17);
        return internalWorkerID;
    }

    /**
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Snowflake Structure<a/>
     */
    public @NotNull Integer getInternalProcessID() {
        if(internalProcessID == null) internalProcessID = (int) ((asLong() & 0x1F000) >> 12);
        return internalProcessID;
    }

    /**
     * For every ID that is generated on that process, this number is incremented
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Snowflake Structure<a/>
     */
    public @NotNull Integer getIncrement() {
        if(increment == null) increment = (int) (asLong() & 0xFFF);
        return increment;
    }

    @Override
    @NotNull
    public String simplify() {
        return asString();
    }

    /**
     * @see #asString()
     */
    @Deprecated
    @Override
    public String toString() {
        return asString();
    }
}
