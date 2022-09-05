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

package me.linusdev.lapi.api.manager.guild.thread;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.events.thread.ThreadListSyncData;
import me.linusdev.lapi.api.communication.gateway.events.thread.ThreadMembersUpdateData;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * When a thread is archived, we receive a {@link GatewayEvent#THREAD_UPDATE THREAD_UPDATE} event.
 *
 *
 *
 */
public class ThreadManagerImpl implements ThreadManager{

    public static final int CHANNEL_HASHMAP_STANDARD_INITIAL_CAPACITY = 16;

    public static final String NEWLY_CREATED_KEY = "newly_created";

    private final @NotNull LApi lApi;

    private boolean initialized = false;
    private int channelHashMapInitialCapacity = CHANNEL_HASHMAP_STANDARD_INITIAL_CAPACITY;

    private @Nullable ConcurrentHashMap<String, ConcurrentHashMap<String, Thread<?>>> channels;
    private @Nullable ConcurrentHashMap<String, Thread<?>> threads;

    public ThreadManagerImpl(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    @Override
    public void init(int initialCapacity) {
        initialized = true;
        channels = new ConcurrentHashMap<>(16);
        threads = new ConcurrentHashMap<>(initialCapacity);
    }

    public void add(Thread<?> thread) {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");
        threads.put(thread.getId(), thread);
        ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.computeIfAbsent(thread.getParentId(),
                s -> new ConcurrentHashMap<>(channelHashMapInitialCapacity));
        threadsInChannel.put(thread.getParentId(), thread);
    }

    @Override
    public @NotNull Update<Thread<?>, Thread<?>> onCreate(@NotNull SOData data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");
        Channel<?> threadChannel = Channel.fromData(lApi, data);

        if(!(threadChannel instanceof Thread)) {
            throw new InvalidDataException(data, "received data has wrong type: " + threadChannel.getType() + ".");
        }

        @Nullable Boolean newlyCreated = (Boolean) data.get(NEWLY_CREATED_KEY);
        @NotNull Thread<?> thread = (Thread<?>) threadChannel;
        @NotNull String parentId = thread.getParentId();

        ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.computeIfAbsent(parentId,
                k -> new ConcurrentHashMap<>(channelHashMapInitialCapacity));
        String threadId = thread.getId();

        if(newlyCreated != null && newlyCreated){
            //This thread is definitely not in the map, because it's new

            threadsInChannel.put(threadId, thread);
            threads.put(threadId, thread);

            return new Update<>(thread, true);

        } else {
            //might already be in the map
            AtomicBoolean alreadyExist = new AtomicBoolean(true);

            Thread<?> threadInMap = threads.computeIfAbsent(threadId, s -> {
                alreadyExist.set(false);
                threadsInChannel.put(threadId, thread);
                return thread;
            });

            if(alreadyExist.get()) {
                threadInMap.updateSelfByData(data);
            }

            return new Update<>(null, threadInMap);
        }
    }

    @Override
    public @NotNull ThreadUpdate onUpdate(@NotNull SOData data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");

        String threadId = (String) data.get(Channel.ID_KEY);

        if(threadId == null) throw new InvalidDataException(data, "missing thread-id", null, Channel.ID_KEY);

        Thread<?> thread = threads.get(threadId);

        if(thread == null) {
            //there is no cached thread with this id.
            //check if the thread is not archived -> it probably just got unarchived -> add it

            Channel<?> channel = Channel.fromData(lApi, data);

            if(!(channel instanceof Thread))
                throw new InvalidDataException(data, "wrong channel type " + channel.getType() + " expected thread.");

            thread = (Thread<?>) channel;

            if(!thread.getThreadMetadata().isArchived()) {

                threads.put(threadId, thread);
                ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.computeIfAbsent(thread.getParentId(),
                        s -> new ConcurrentHashMap<>(channelHashMapInitialCapacity));
                threadsInChannel.put(threadId, thread);
                return new ThreadUpdate(null, thread, false, true);
            }

            return new ThreadUpdate(null, thread, false, false);
        }

        boolean wasArchived = thread.getThreadMetadata().isArchived();
        Thread<?> copy = lApi.isCopyOldThreadOnUpdateEventEnabled() ? thread.copy() : null;
        thread.updateSelfByData(data);

        if(!lApi.isDoNotRemoveArchivedThreadsEnabled() && thread.getThreadMetadata().isArchived()) {
            //remove thread if it was archived
            threads.remove(threadId);
            ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.get(thread.getParentId());
            if(threadsInChannel != null) threadsInChannel.remove(threadId);
        }

        return new ThreadUpdate(copy, thread, !wasArchived && thread.getThreadMetadata().isArchived(), true);
    }

    /**
     * Received, when a thread relevant to the current user is deleted.
     * The {@link SOData} only contains the id, guild_id, parent_id, and type fields.
     * @param data {@link SOData} containing just the id, guild_id, parent_id, and type fields.
     * @return {@link Thread} which was removed from this manager or {@code null} if there was no thread with given id.
     * @throws InvalidDataException if id field is missing in given data
     */
    public @Nullable Thread<?> onDelete(@NotNull SOData data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");

        String threadId = (String) data.get(Channel.ID_KEY);

        if(threadId == null) throw new InvalidDataException(data, "missing thread id", null, Channel.ID_KEY);

        Thread<?> removed = threads.remove(threadId);

        if(removed != null) {
            ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.get(removed.getParentId());
            if(threadsInChannel != null) {
                //should never be null, but is checked anyway just in case
                threadsInChannel.remove(threadId);
            }
        }

        return removed;
    }

    @Override
    public synchronized @NotNull ListUpdate<Thread<?>> onThreadListSync(@NotNull ThreadListSyncData threadListSyncData) throws InvalidDataException{
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");

        if(lApi.isDoNotRemoveArchivedThreadsEnabled() || threads.isEmpty()) {
            @NotNull ArrayList<Thread<?>> finalAdded = threads.isEmpty() ? new ArrayList<>(threadListSyncData.getThreads().size()) : new ArrayList<>();
            for(Thread<?> thread : threadListSyncData.getThreads()) {
                threads.computeIfAbsent(thread.getId(), s -> {
                    ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.computeIfAbsent(thread.getParentId(),
                            k -> new ConcurrentHashMap<>(channelHashMapInitialCapacity));
                    threadsInChannel.put(thread.getId(), thread);
                    finalAdded.add(thread);
                    return thread;
                });

            }
            return new ListUpdate<>(null, null, finalAdded, null);
        }

        @Nullable ArrayList<Thread<?>> added = null;
        @Nullable ArrayList<Thread<?>> removed = null;

        @NotNull HashMap<String, List<Thread<?>>> processed = new HashMap<>();
        if(threadListSyncData.getChannelIds() != null){
            processed = new HashMap<>(threadListSyncData.getChannelIds().size());
            for(Snowflake channelId : threadListSyncData.getChannelIds())
                processed.put(channelId.asString(), new LinkedList<>());
        }

        for(Thread<?> thread : threadListSyncData.getThreads()) {
            //We shouldn't receive archived Threads, but let's check it anyway
            if(!lApi.isDoNotRemoveArchivedThreadsEnabled() && thread.getThreadMetadata().isArchived()) continue;

            processed.computeIfAbsent(thread.getParentId(), s -> new LinkedList<>())
                    .add(thread);
        }


        for(Map.Entry<String, List<Thread<?>>> entry : processed.entrySet()) {
            String channelId = entry.getKey();
            List<Thread<?>> list = entry.getValue();

            ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.get(channelId);

            if(threadsInChannel == null) {
                //All Threads in this channel are new
                threadsInChannel = new ConcurrentHashMap<>(list.size());
                channels.put(channelId, threadsInChannel);

                for(Thread<?> thread : list) {
                    threads.put(thread.getId(), thread);
                    threadsInChannel.put(thread.getId(), thread);
                    if(added == null) added = new ArrayList<>();
                    added.add(thread);
                }
            } else {

                for(Thread<?> cachedThread : threadsInChannel.values()) {

                    //check whether the cached thread, is also inside the retrieved list
                    //if it is, remove it from the retrieved list, so in the end it will only contain, the
                    //threads, which we have to add to the cache.
                    boolean found = false;
                    for(int i = 0; i < list.size(); i++) {
                        Thread<?> thread = list.get(i);
                        if(cachedThread.getId().equals(thread.getId())) {
                            list.remove(i);
                            found = true;
                            break;
                        }
                    }

                    //If it was not in the retrieved list, remove it from the cache
                    if(!found) {
                        threadsInChannel.remove(cachedThread.getId());
                        threads.remove(cachedThread.getId());
                        if(removed == null) removed = new ArrayList<>();
                        removed.add(cachedThread);
                    }

                    //Add the remaining threads to the cache
                    for(Thread<?> thread : list) {
                        threads.put(thread.getId(), thread);
                        threadsInChannel.put(thread.getId(), thread);
                        if(added == null) added = new ArrayList<>();
                        added.add(thread);
                    }
                }
            }
        }

        return new ListUpdate<>(null, null, added, removed);
    }

    @Override
    public @NotNull ThreadMemberUpdate onThreadMemberUpdate(@NotNull SOData data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");

        ThreadMember threadMember = ThreadMember.fromData(data);
        String threadId = threadMember.getId();

        if(threadId == null)
            throw new InvalidDataException(data, "thread id is null.");

        Thread<?> thread = threads.get(threadId);

        if(thread == null) {
            return new ThreadMemberUpdate(null, null, threadMember);
        }

        return new ThreadMemberUpdate(thread, thread.updateMember(threadMember), threadMember);
    }

    @Override
    public void onThreadMembersUpdate(@NotNull ThreadMembersUpdateData threadMembersUpdateData) throws InvalidDataException {
        //TODO: Maybe add a cache for thread members
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public @Nullable Thread<?> getThread(@NotNull String id) {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");
        return threads.get(id);
    }

    @Override
    public @Nullable Thread<?> getThread(@NotNull String channelId, @NotNull String id) {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");

        ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.get(channelId);

        if(threadsInChannel == null) return null;

        return threadsInChannel.get(id);
    }
}
