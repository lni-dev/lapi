package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is a timestamp often used by discord.<br>
 * You can get its milliseconds since 01.01.1970 00:00:00+00:00 using {@link #toEpochMillis()}
 *
 * @see <a href="https://discord.com/developers/docs/reference#iso8601-datetime" target="_top">ISO8601 Date/Time</a>
 * @see DateTimeFormatter#ISO_OFFSET_DATE_TIME
 */
public class ISO8601Timestamp implements SimpleDatable {

    //2021-11-25T22:22:47.664000+00:00
    public static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    //DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxxxx"); I guess there already is one from java .-.

    private @NotNull final String timestamp;

    /**
     *
     * @param timestamp String of the timestamp in this format "2021-11-25T22:22:47.664000+00:00"
     */
    public ISO8601Timestamp(@NotNull String timestamp){
        this.timestamp = timestamp;
    }

    /**
     *
     * @return {@link OffsetDateTime} of this timestamp
     */
    public @NotNull OffsetDateTime toOffsetDateTime(){
        return OffsetDateTime.parse(timestamp, ISO8601_FORMATTER);
    }

    /**
     *
     * @return {@link Instant} of this timestamp
     */
    public @NotNull Instant toInstant(){
        return toOffsetDateTime().toInstant();
    }

    /**
     *
     * @return @return the milli-seconds since 01.01.1970 00:00:00+00:00 of this timestamp
     */
    public long toEpochMillis(){
        return toInstant().toEpochMilli();
    }

    /**
     *
     * @return the seconds since 01.01.1970 00:00:00+00:00 of this timestamp
     */
    public long toEpochSeconds(){
        return toInstant().getEpochSecond();
    }

    /**
     *
     * @param timestamp ISO8601 Timestamp as {@link String} or {@code null}
     * @return {@link ISO8601Timestamp} or {@code null} if timestamp parameter was {@code null}
     */
    public static @Nullable ISO8601Timestamp fromString(@Nullable String timestamp){
        if(timestamp == null) return null;
        return new ISO8601Timestamp(timestamp);
    }

    /**
     *
     * @return the String of the timestamp, retrieved from Discord
     */
    public @NotNull String getTimestamp() {
        return timestamp;
    }

    @Override
    public @NotNull Object simplify() {
        return timestamp;
    }

    /**
     *
     * @return the ISO8601 timestamp string
     */
    @Override
    public String toString() {
        return timestamp;
    }
}
