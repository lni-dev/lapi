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

package me.linusdev.lapi.api.event;

import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.AnyEventListener;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ReadyEventAwaiter implements AnyEventListener {


    private final Map<EventIdentifier, EventAwaiter> events;

    public ReadyEventAwaiter(@NotNull LApiImpl lApi) {
        this.events = new ConcurrentHashMap<>();

        for(EventIdentifier identifier : EventIdentifier.READY_EVENTS) {
            if(identifier.isPresent(lApi)) {
                events.put(identifier, new EventAwaiter());
            }
        }

        lApi.getEventTransmitter().addAnyEventListener(this);
    }

    public void forEachAwaiter(@NotNull BiConsumer<EventIdentifier, EventAwaiter> consumer) {
        for(Map.Entry<EventIdentifier, EventAwaiter> entry : events.entrySet()) {
            if(entry.getKey().isRequiredForLApiReady()) {
                consumer.accept(entry.getKey(), entry.getValue());
            }
        }
    }

    public @Nullable EventAwaiter getAwaiter(@NotNull EventIdentifier identifier) {
        return events.get(identifier);
    }

    /**
     * Checks if all events, required for the {@link EventIdentifier#LAPI_READY}, event have been triggered.
     * @return {@code true} if all events, required for the {@link EventIdentifier#LAPI_READY} event, have been triggered.
     */
    private boolean checkLApiReady() {
        for(Map.Entry<EventIdentifier, EventAwaiter> entry : events.entrySet()) {
            if(entry.getKey().isRequiredForLApiReady()) {
                if(!entry.getValue().hasTriggered()) return false;
            }
        }

        return true;
    }

    @Override
    public void onEvent(@NotNull LApi lApi, @NotNull Event event, @NotNull EventIdentifier identifier) {
        final EventAwaiter awaiter = events.get(identifier);
        if(awaiter != null) {
            awaiter.trigger();
        }
    }
}
