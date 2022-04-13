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

import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreadUpdate extends Update<Thread<?>, Thread<?>> {

    private final boolean gotArchived;

    public ThreadUpdate(@Nullable Thread<?> copy, @NotNull Thread<?> thread, boolean gotArchived) {
        super(copy, thread);
        this.gotArchived = gotArchived;
    }

    /**
     * Only working properly if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_THREADS CACHE_THREADS} is enabled.
     *
     * @return {@code true} if the thread got archived.
     */
    public boolean gotArchived() {
        return gotArchived;
    }
}
