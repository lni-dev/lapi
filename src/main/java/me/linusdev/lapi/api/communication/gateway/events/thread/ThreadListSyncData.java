/*
 * Copyright (c) 2022 Linus Andera
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

import me.linusdev.data.Datable;
import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#thread-list-sync-thread-list-sync-event-fields"
 * target="_top">Thread List Sync Event Fields</a>
 */
public class ThreadListSyncData implements Datable, HasLApi {

    public static final String GUILD_ID_KEY = "guild_id";
    public static final String CHANNEL_IDS_KEY = "channel_ids";
    public static final String THREADS_KEY = "threads";
    public static final String MEMBERS_KEY = "members";

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake guildId;
    private final @Nullable ArrayList<Snowflake> channelIds;
    private final @NotNull ArrayList<Thread<?>> threads;
    private final @NotNull ArrayList<ThreadMember> members;

    /**
     * @param lApi       {@link LApi}
     * @param guildId    the id of the guild
     * @param channelIds the parent channel ids whose threads are being synced. If omitted, then threads were synced
     *                   for the entire guild. This array may contain channel_ids that have no active threads as well,
     *                   so you know to clear that data.
     * @param threads    all active threads in the given channels that the current user can access
     * @param members    all thread member objects from the synced threads for the current user,
     *                   indicating which threads the current user has been added to
     */
    public ThreadListSyncData(@NotNull LApi lApi, @NotNull Snowflake guildId, @Nullable ArrayList<Snowflake> channelIds,
                              @NotNull ArrayList<Thread<?>> threads, @NotNull ArrayList<ThreadMember> members) {
        this.lApi = lApi;
        this.guildId = guildId;
        this.channelIds = channelIds;
        this.threads = threads;
        this.members = members;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable ThreadListSyncData fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if (data == null) return null;

        String guildId = (String) data.get(GUILD_ID_KEY);

        ArrayList<Snowflake> channelIds = data.getListAndConvertWithException(CHANNEL_IDS_KEY,
                (ExceptionConverter<String, Snowflake, InvalidDataException>) Snowflake::fromString);

        ArrayList<Thread<?>> threads = data.getListAndConvertWithException(THREADS_KEY,
                (ExceptionConverter<SOData, Thread<?>, InvalidDataException>) convertible -> {
                    Channel<?> channel = Channel.fromData(lApi, convertible);
                    if(!(channel instanceof Thread))
                        throw new InvalidDataException(convertible, "wrong type: " + channel.getType() + ". expected Thread!");
                    return (Thread<?>) channel;
                });

        ArrayList<ThreadMember> members = data.getListAndConvertWithException(MEMBERS_KEY, ThreadMember::fromData);

        if(guildId == null || threads == null || members == null) {
            InvalidDataException.throwException(data, null, ThreadListSyncData.class,
                    new Object[]{guildId, threads, members},
                    new String[]{GUILD_ID_KEY, THREADS_KEY, MEMBERS_KEY});
            return null; //appeasing null checks - This line will never be executed
        }

        return new ThreadListSyncData(lApi, Snowflake.fromString(guildId), channelIds, threads, members);
    }

    /**
     * the id of the guild as {@link Snowflake}
     */
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * the id of the guild
     */
    public @NotNull String getGuildId() {
        return guildId.asString();
    }

    /**
     * the parent channel ids whose threads are being synced. If omitted, then threads were synced for the entire guild.
     * This array may contain channel_ids that have no active threads as well, so you know to clear that data.
     */
    public @Nullable ArrayList<Snowflake> getChannelIds() {
        return channelIds;
    }

    /**
     * all active threads in the given channels that the current user can access
     */
    public @NotNull ArrayList<Thread<?>> getThreads() {
        return threads;
    }

    /**
     * all thread member objects from the synced threads for the current user, indicating which threads
     * the current user has been added to
     */
    public @NotNull ArrayList<ThreadMember> getMembers() {
        return members;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(GUILD_ID_KEY, guildId);
        data.addIfNotNull(CHANNEL_IDS_KEY, channelIds);
        data.add(THREADS_KEY, threads);
        data.add(MEMBERS_KEY, members);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
