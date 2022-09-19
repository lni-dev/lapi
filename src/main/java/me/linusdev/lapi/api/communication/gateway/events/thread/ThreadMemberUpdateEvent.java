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
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.manager.guild.thread.ThreadMemberUpdate;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * thread member for the current user was updated
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#thread-member-update" target="_top">Thread Member Update</a>
 */
public class ThreadMemberUpdateEvent extends Event {

    private final @NotNull ThreadMemberUpdate update;

    public ThreadMemberUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                                   @NotNull ThreadMemberUpdate update) {
        super(lApi, payload, guildId);
        this.update = update;
    }

    public @NotNull ThreadMember getMember() {
        return update.getUpdated();
    }

    public @Nullable ThreadMember getOldMember() {
        return update.getOld();
    }

    /**
     * The cached thread containing this thread member or {@code null}
     */
    public @Nullable Thread<?> getThread() {
        return update.getThread();
    }
}
