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

package me.linusdev.lapi.api.manager.presence;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.objects.presence.PresenceUpdate;
import org.jetbrains.annotations.NotNull;

public interface PresenceManager extends PresencePool, Manager {

    void add(@NotNull SOData presenceData) throws InvalidDataException;

    /**
     *
     * {@link Update#isNew()} will be {@code true}, if the presence was not contained in this {@link PresenceManager}
     *
     * @param data {@link SOData} for the {@link PresenceUpdate}
     * @return {@link Update} containing the updated presence and a copy of the old presence if possible.
     * @throws InvalidDataException see implementation
     */
    @NotNull Update<PresenceUpdate, PresenceUpdate> onUpdate(@NotNull SOData data) throws InvalidDataException;
}
