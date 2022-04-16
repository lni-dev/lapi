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

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.guild.thread.ThreadUpdate;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Received when a thread is updated or unarchived.
 *
 * @see <a href="https://discord.com/developers/docs/topics/threads#additional-context-on-the-the-threadlistsync-and-threadcreate-dispatches" target="_top">
 *     Additional context on the the THREAD_LIST_SYNC and THREAD_CREATE dispatches</a>
 */
public class ThreadUpdateEvent extends Event {

    private final @NotNull ThreadUpdate update;

    public ThreadUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                             @NotNull ThreadUpdate update) {
        super(lApi, payload, guildId);

        this.update = update;
    }

    public @NotNull ThreadUpdate getUpdate() {
        return update;
    }

    public @NotNull Thread<?> getThread() {
        return update.getObj();
    }

    /**
     * This may be {@code null} even if {@link ConfigFlag#COPY_THREAD_ON_UPDATE_EVENT COPY_THREAD_ON_UPDATE_EVENT} is enabled!
     */
    public @Nullable Thread<?> getOldThread() {
        return update.getCopy();
    }
}
