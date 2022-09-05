/*
 * Copyright (c) 2021-2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.objects.timestamp;

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * This is a timestamp often used by discord.<br>
 * You can get its milliseconds since 01.01.1970 00:00:00+00:00 using {@link #toEpochMillis()}
 *
 * @see <a href="https://discord.com/developers/docs/reference#iso8601-datetime" target="_top">ISO8601 Date/Time</a>
 * @see DateTimeFormatter#ISO_OFFSET_DATE_TIME
 */
public class ISO8601Timestamp implements SimpleDatable, Copyable<ISO8601Timestamp> {

    //2021-11-25T22:22:47.664000+00:00
    public static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static final DateTimeFormatter FORMATTER_TO_ISO8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxxxx");
    // the last one won#t use Z for a +00:00 offset, but +00:00 instead, like Discord does too

    private @NotNull final String timestamp;

    /**
     *
     * @param timestamp String of the timestamp in this format "2021-11-25T22:22:47.664000+00:00"
     */
    public ISO8601Timestamp(@NotNull String timestamp){
        this.timestamp = timestamp;
    }

    /**
     * This will create a new {@link ISO8601Timestamp} of given milliseconds
     *
     * @param millis the millisecond since 01.01.1970 00:00:00+00:00
     * @return {@link ISO8601Timestamp}
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static  ISO8601Timestamp of(long millis){
        OffsetDateTime offsetDateTime = Instant.ofEpochMilli(millis).atOffset(ZoneOffset.ofHoursMinutes(0, 0));
        return new ISO8601Timestamp(offsetDateTime.format(FORMATTER_TO_ISO8601));
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

    /**
     *
     * @return this, because it is immutable
     */
    @Override
    public @NotNull ISO8601Timestamp copy() {
        return this;
    }
}
