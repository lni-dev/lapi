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

package me.linusdev.lapi.api.manager.presence;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.presence.PresenceUpdate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class PresenceManagerImpl implements PresenceManager{

    private final LApiImpl lApi;
    private boolean initialized = false;
    private @Nullable ConcurrentHashMap<String, PresenceUpdate> presences;

    public PresenceManagerImpl(LApiImpl lApi) {
        this.lApi = lApi;
    }

    @Override
    public void init(int initialCapacity) {
        initialized = true;
        presences = new ConcurrentHashMap<>(initialCapacity);
    }

    @Override
    public void add(@NotNull SOData presenceData) throws InvalidDataException {
        if(presences == null) throw new UnsupportedOperationException("init() not yet called.");
        PresenceUpdate presence = PresenceUpdate.fromData(presenceData);
        if(presence.getUser() == null) throw new InvalidDataException(presenceData, "Presence without user id...");
        presences.put(presence.getUser().getId(), presence);
    }

    @Override
    public @NotNull Update<PresenceUpdate, PresenceUpdate> onUpdate(@NotNull SOData data) throws InvalidDataException {
        if(presences == null) throw new UnsupportedOperationException("init() not yet called.");
        PresenceUpdate presence = PresenceUpdate.fromData(data);
        if(presence.getUser() == null) throw new InvalidDataException(data, "Presence without user id...");

        PresenceUpdate cached = presences.get(presence.getUser().getId());

        if(cached == null) {
            presences.put(presence.getUser().getId(), presence);

            return new Update<>(presence, true);
        }

        if(lApi.isCopyOldPresenceOnUpdateEventEnabled()) {
            return new Update<>(cached, data);
        }

        cached.updateSelfByData(data);
        return new Update<>(null, cached);
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
