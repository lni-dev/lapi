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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventUserAddRemoveData;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.list.ListManager;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.lapi.api.other.LApiImplConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

public class GuildScheduledEventManagerImpl extends ListManager<GuildScheduledEvent> implements GuildScheduledEventManager{


    public GuildScheduledEventManagerImpl(@NotNull LApiImpl lApi) {
        super(lApi, GuildScheduledEvent.ID_KEY, GuildScheduledEvent::fromData, lApi::isCopyOldGuildScheduledEventOnUpdateEventEnabled);
    }

    public @Nullable GuildScheduledEvent onUserAdd(@NotNull GuildScheduledEventUserAddRemoveData data) {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");

        return objects.get(data.getScheduledEventId());
    }

    public @Nullable GuildScheduledEvent onUserRemove(@NotNull GuildScheduledEventUserAddRemoveData data) {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");

        return objects.get(data.getScheduledEventId());
    }

}
