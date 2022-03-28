/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.objects.snowflake;

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static me.linusdev.lapi.api.communication.DiscordApiCommunicationHelper.DISCORD_EPOCH;

/**
 *
 * @see <a href="https://discord.com/developers/docs/reference#snowflakes" target="_top">Snowflakes</a>
 */
public class Snowflake implements SimpleDatable, Copyable<Snowflake> {
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
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Snowflake Structure</a>
     */
    public @NotNull Integer getInternalWorkerID() {
        if(internalWorkerID == null) internalWorkerID = (int) ((asLong() & 0x3E0000) >> 17);
        return internalWorkerID;
    }

    /**
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Snowflake Structure</a>
     */
    public @NotNull Integer getInternalProcessID() {
        if(internalProcessID == null) internalProcessID = (int) ((asLong() & 0x1F000) >> 12);
        return internalProcessID;
    }

    /**
     * For every ID that is generated on that process, this number is incremented
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Snowflake Structure</a>
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

    /**
     * This will not return an actual copy of this object, but the same object instead. <br>
     * That is not a problem, because this class is completely constant and cannot be changed after creation.
     * @return this
     */
    @Override
    public @NotNull Snowflake copy() {
        return this;
    }

    /**
     *
     * @param id string id
     * @return {@code true} if given id equals {@link #asString()}, {@code false} otherwise
     */
    public boolean idEquals(@Nullable String id){
        if(id == null) return false;
        return this.string.equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snowflake snowflake = (Snowflake) o;
        return string.equals(snowflake.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
    }
}
