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
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ThreadManagerImpl implements ThreadManager{

    private final @NotNull LApi lApi;

    private boolean initialized = false;

    private @Nullable HashMap<String, HashMap<String, Thread<?>>> channels;
    private @Nullable HashMap<String, Thread<?>> threads;

    public ThreadManagerImpl(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    @Override
    public void init(int initialCapacity) {
        initialized = true;
        channels = new HashMap<>(initialCapacity);
    }

    /**
     * received when added to the thread (for example message with @current-user)
     * -> the manager might already know of this Thread (from GUILD_CREATE)
     * -> check newly_created field!
     * -> Thread Member Update when current user is added (or updated) to a thread
     * -> Thread Members Update anyone added or removed from a thread
     * @param data
     * @return
     * @throws InvalidDataException
     */
    public Thread<?> onCreate(@NotNull Data data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");
        Channel<?> threadChannel = Channel.fromData(lApi, data);

        if(!(threadChannel instanceof Thread)) {
            throw new InvalidDataException(data, "received data has wrong type: " + threadChannel.getType() + ".");
        }

        Thread<?> thread = (Thread<?>) threadChannel;

        String parentId = thread.getParentId();

        HashMap<String, Thread<?>> threadsInChannel
                = channels.computeIfAbsent(parentId, k -> new HashMap<>(1));

        String threadId = thread.getId();
        threadsInChannel.put(threadId, thread);
        threads.put(threadId, thread);

        return thread;
    }

    public Thread<?> onUpdate(@NotNull Data data) throws InvalidDataException {
        if(channels == null || threads == null) throw new UnsupportedOperationException("init() not yet called");

        return null;
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
