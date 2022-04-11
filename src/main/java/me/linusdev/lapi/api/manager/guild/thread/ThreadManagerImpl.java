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

package me.linusdev.lapi.api.manager.guild.thread;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.updatable.Updatable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadManagerImpl implements ThreadManager{

    public static final String NEWLY_CREATED_KEY = "newly_created";

    private final @NotNull LApi lApi;

    private boolean initialized = false;

    private @Nullable ConcurrentHashMap<String, ConcurrentHashMap<String, Thread<?>>> channels;
    private @Nullable ConcurrentHashMap<String, Thread<?>> threads;

    public ThreadManagerImpl(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    @Override
    public void init(int initialCapacity) {
        initialized = true;
        channels = new ConcurrentHashMap<>(initialCapacity);
    }

    /**
     * Received when a new Thread is created (then newly_created field exists) or
     * when the current user is added to this (private) thread.
     * @param data thread data
     * @return {@link Thread} contained in this manager
     * @throws InvalidDataException if data parameter is invalid
     */
    public @NotNull Thread<?> onCreate(@NotNull Data data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");
        Channel<?> threadChannel = Channel.fromData(lApi, data);

        if(!(threadChannel instanceof Thread)) {
            throw new InvalidDataException(data, "received data has wrong type: " + threadChannel.getType() + ".");
        }

        @Nullable Boolean newlyCreated = (Boolean) data.get(NEWLY_CREATED_KEY);
        @NotNull Thread<?> thread = (Thread<?>) threadChannel;
        @NotNull String parentId = thread.getParentId();

        ConcurrentHashMap<String, Thread<?>> threadsInChannel = channels.computeIfAbsent(parentId, k -> new ConcurrentHashMap<>(1));
        String threadId = thread.getId();

        if(newlyCreated != null && newlyCreated){
            //This thread is definitely not in the map, because it's new

            threadsInChannel.put(threadId, thread);
            threads.put(threadId, thread);

            return thread;

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

            return threadInMap;
        }
    }

    /**
     * Received when a thread is updated. (not when last_message_id changes)
     * @param data {@link Data} to {@link Updatable#updateSelfByData(Data) update}
     * @return updated {@link Thread} or {@code null} if no thread with id given in data was found.
     * @throws InvalidDataException if id is missing in data or in {@link Updatable#updateSelfByData(Data)}
     */
    public Thread<?> onUpdate(@NotNull Data data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");

        String threadId = (String) data.get(Channel.ID_KEY);

        if(threadId == null) throw new InvalidDataException(data, "missing thread-id", null, Channel.ID_KEY);

        Thread<?> thread = threads.get(threadId);

        if(thread == null) {
            //there is no cached thread with this id.
            return null;
        }

        thread.updateSelfByData(data);
        return thread;
    }

    /**
     * Received, when a thread relevant to the current user is deleted.
     * The {@link Data} only contains the id, guild_id, parent_id, and type fields.
     * @param data {@link Data} containing just the id, guild_id, parent_id, and type fields.
     * @return {@link Thread} which was removed from this manager or {@code null} if there was no thread with given id.
     * @throws InvalidDataException if id field is missing in given data
     */
    public Thread<?> onDelete(@NotNull Data data) throws InvalidDataException {
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
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
