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

package me.linusdev.lapi.api.communication.gateway.events.thread;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.Converter;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#thread-members-update-thread-members-update-event-fields" target="_top">Thread Members Update Event Fields</a>
 */
public class ThreadMembersUpdateData implements Datable {

    public static final String ID_KEY = "id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String MEMBER_COUNT_KEY = "member_count";
    public static final String ADDED_MEMBERS_KEY = "added_members";
    public static final String REMOVED_MEMBER_IDS_KEY = "removed_member_ids";

    private final @NotNull Snowflake id;
    private final @NotNull Snowflake guildId;
    private final int memberCount;
    private final @Nullable ArrayList<Data> addedMembers;
    private final @Nullable ArrayList<Snowflake> removedMemberIds;

    public ThreadMembersUpdateData(@NotNull Snowflake id, @NotNull Snowflake guildId, int memberCount,
                                   @Nullable ArrayList<Data> addedMembers, @Nullable ArrayList<Snowflake> removedMemberIds) {
        this.id = id;
        this.guildId = guildId;
        this.memberCount = memberCount;
        this.addedMembers = addedMembers;
        this.removedMemberIds = removedMemberIds;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable ThreadMembersUpdateData fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        Number memberCount = (Number) data.get(MEMBER_COUNT_KEY);

        ArrayList<Data> addedMembers = data.getAndConvertArrayList(ADDED_MEMBERS_KEY,
                (Converter<Object, Data>) convertible -> (Data) convertible);

        ArrayList<Snowflake> removedMemberIds = data.getAndConvertArrayList(REMOVED_MEMBER_IDS_KEY,
                (Converter<String, Snowflake>) Snowflake::fromString);

        if(id == null || guildId == null || memberCount == null) {
            InvalidDataException.throwException(data, null, ThreadMembersUpdateData.class,
                    new Object[]{id, guildId, memberCount},
                    new String[]{ID_KEY, GUILD_ID_KEY, MEMBER_COUNT_KEY});
            return null; //will never be executed
        }

        return new ThreadMembersUpdateData(
                Snowflake.fromString(id),
                Snowflake.fromString(guildId),
                memberCount.intValue(), addedMembers, removedMemberIds);
    }

    /**
     * the id of the guild as {@link Snowflake}
     */
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * the id of the guild as {@link String}
     */
    public @NotNull String getGuildId() {
        return guildId.asString();
    }

    /**
     * the id of the thread as {@link Snowflake}
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the id of the thread as {@link String}
     */
    public @NotNull String getId() {
        return id.asString();
    }

    /**
     * the users who were added to the thread<br>
     * In this gateway event, the thread member objects will also include the guild member
     * and nullable presence objects for each added thread member
     */
    public @Nullable ArrayList<Data> getAddedMembers() {
        return addedMembers;
    }

    /**
     * the id of the users who were removed from the thread
     */
    public @Nullable ArrayList<Snowflake> getRemovedMemberIds() {
        return removedMemberIds;
    }

    /**
     * the approximate number of members in the thread, capped at 50
     */
    public int getMemberCount() {
        return memberCount;
    }

    @Override
    public Data getData() {
        Data data = new Data(5);

        data.add(ID_KEY, id);
        data.add(GUILD_ID_KEY, guildId);
        data.add(MEMBER_COUNT_KEY, memberCount);
        data.addIfNotNull(ADDED_MEMBERS_KEY, addedMembers);
        data.addIfNotNull(REMOVED_MEMBER_IDS_KEY, removedMemberIds);

        return data;
    }
}
