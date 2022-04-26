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

package me.linusdev.lapi.api.manager.guild.scheduledevent;

import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class GuildScheduledEventManagerImpl implements GuildScheduledEventManager{

    private final @NotNull LApi lApi;
    private boolean initialized = false;

    private @Nullable HashMap<String, GuildScheduledEvent> events;

    public GuildScheduledEventManagerImpl(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    @Override
    public void init(int initialCapacity) {
        this.initialized = true;
        events = new HashMap<>(initialCapacity);
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
