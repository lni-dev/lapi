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

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.manager.guild.thread.ThreadManager;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThreadListSyncEvent extends Event {

    private final @NotNull ThreadListSyncData threadListSyncData;
    private final @Nullable ListUpdate<Channel> update;

    public ThreadListSyncEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                               @NotNull ThreadListSyncData threadListSyncData, @Nullable ListUpdate<Channel> update) {
        super(lApi, payload, guildId);
        this.threadListSyncData = threadListSyncData;
        this.update = update;
    }

    /**
     * {@link ListUpdate#getOld()} and {@link ListUpdate#getUpdated()} will always return {@code null}#
     * (see {@link ThreadManager#onThreadListSync(ThreadListSyncData)})
     * @see #getRemoved()
     * @see #getAdded()
     * @return {@link ListUpdate} or {@code null} if {@link ConfigFlag#CACHE_THREADS CACHE_THREADS} is not enabled.
     */
    public @Nullable ListUpdate<Channel> getUpdate() {
        return update;
    }

    /**
     * All threads that have been removed from the cache due to this list sync event
     */
    public @Nullable List<Channel> getRemoved() {
        if(update == null) return null;
        return update.getRemoved();
    }

    /**
     * All threads that have been added to the cache due to this list sync event
     */
    public @Nullable List<Channel> getAdded() {
        if(update == null) return null;
        return update.getAdded();
    }
}
