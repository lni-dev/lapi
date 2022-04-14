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

package me.linusdev.lapi.api.objects.channel.thread;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A thread member is used to indicate whether a user has joined a thread or not.
 * @see <a href="https://discord.com/developers/docs/resources/channel#thread-member-object" target="_top"></a>
 */
public class ThreadMember implements Copyable<ThreadMember>, Datable {

    public static final String ID_KEY = "id";
    public static final String USER_ID_KEY = "user_id";
    public static final String JOIN_TIMESTAMP_KEY = "join_timestamp";
    public static final String FLAGS_KEY = "flags";

    private final @Nullable Snowflake id;
    private final @Nullable Snowflake userId;
    private final @NotNull ISO8601Timestamp joinTimestamp;
    private final int flags;

    /**
     *
     * @param id the id of the thread
     * @param userId the id of the user
     * @param joinTimestamp the time the current user last joined the thread
     * @param flags any user-thread settings, currently only used for notifications
     */
    public ThreadMember(@Nullable Snowflake id, @Nullable Snowflake userId, @NotNull ISO8601Timestamp joinTimestamp, int flags) {
        this.id = id;
        this.userId = userId;
        this.joinTimestamp = joinTimestamp;
        this.flags = flags;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link ThreadMember}
     * @throws InvalidDataException if {@link #JOIN_TIMESTAMP_KEY} or {@link #FLAGS_KEY} are missing or null
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ThreadMember fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String userId = (String) data.get(USER_ID_KEY);
        String joinTimestamp = (String) data.get(JOIN_TIMESTAMP_KEY);
        Number flags = (Number) data.get(FLAGS_KEY);

        if(joinTimestamp == null || flags == null){
            InvalidDataException.throwException(data, null, ThreadMember.class,
                    new Object[]{joinTimestamp, flags},
                    new String[]{JOIN_TIMESTAMP_KEY, FLAGS_KEY});
            return null; //this will never happen, because above method will throw an Exception
        }

        return new ThreadMember(Snowflake.fromString(id), Snowflake.fromString(userId), ISO8601Timestamp.fromString(joinTimestamp), flags.intValue());
    }

    /**
     * the id as {@link Snowflake} of the thread
     */
    public @Nullable Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the id as {@link String} of the thread
     */
    public @Nullable String getId() {
        if(id == null) return null;
        return id.asString();
    }

    /**
     * the id as {@link Snowflake} of the user
     */
    public @Nullable Snowflake getUserIdAsSnowflake() {
        return userId;
    }

    /**
     * the id as {@link String} of the user
     */
    public @Nullable String getUserId() {
        if(userId == null) return null;
        return userId.asString();
    }

    /**
     * the time the current user last joined the thread
     */
    public @NotNull ISO8601Timestamp getJoinTimestamp() {
        return joinTimestamp;
    }

    /**
     * any user-thread settings, currently only used for notifications
     */
    public int getFlags() {
        return flags;
    }

    /**
     *
     * @return {@link Data} for this {@link ThreadMember}
     */
    @Override
    public Data getData() {
        Data data = new Data(4);

        data.add(ID_KEY, id);
        data.add(USER_ID_KEY, userId);
        data.add(JOIN_TIMESTAMP_KEY, joinTimestamp);
        data.add(FLAGS_KEY, flags);

        return data;
    }

    /**
     *
     * @return this, because everything in this class is final
     */
    @Override
    public @NotNull ThreadMember copy() {
        return this;
    }
}
