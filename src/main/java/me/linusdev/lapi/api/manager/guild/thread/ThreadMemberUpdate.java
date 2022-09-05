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

import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreadMemberUpdate {

    private final @Nullable Thread<?> thread;
    private final @Nullable ThreadMember old;
    private final @NotNull ThreadMember updated;

    public ThreadMemberUpdate(@Nullable Thread<?> thread, @Nullable ThreadMember old, @NotNull ThreadMember updated) {
        this.thread = thread;
        this.old = old;
        this.updated = updated;
    }

    public @Nullable Thread<?> getThread() {
        return thread;
    }

    public @NotNull ThreadMember getUpdated() {
        return updated;
    }

    public @Nullable ThreadMember getOld() {
        return old;
    }
}
