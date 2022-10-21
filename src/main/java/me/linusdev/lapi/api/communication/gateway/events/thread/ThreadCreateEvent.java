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
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Received, when a {@link #isNewlyCreated() new thread is created} or when the current user (your bot) is being added to a thread
 * @see <a href="https://discord.com/developers/docs/topics/threads#additional-context-on-the-the-threadlistsync-and-threadcreate-dispatches" target="_top">
 *     Additional context on the the THREAD_LIST_SYNC and THREAD_CREATE dispatches</a>
 */
public class ThreadCreateEvent extends Event {

    private final @NotNull Update<Channel, Channel> update;

    public ThreadCreateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                             @NotNull Update<Channel, Channel> update) {
        super(lApi, payload, guildId);
        this.update = update;
    }

    public boolean isNewlyCreated(){
        return update.isNew();
    }

    public Channel getThread() {
        return update.getObj();
    }
}
