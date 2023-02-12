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

package me.linusdev.lapi.api.communication.gateway.events.transmitter;

import de.linusdev.lutils.llist.LLinkedList;
import org.jetbrains.annotations.NotNull;


public class EventIdentifierList {

    final LLinkedList<?>[] listenersMap;
    final Object writeLock = new Object();

    public EventIdentifierList() {
        listenersMap = new LLinkedList[EventIdentifier.values().length];
    }

    @SuppressWarnings("unchecked")
    public LLinkedList<EventListener> get(@NotNull EventIdentifier identifier) {
        return (LLinkedList<EventListener>) listenersMap[identifier.ordinal()];
    }

    public void put(@NotNull EventIdentifier identifier, @NotNull EventListener listener) {
        synchronized (writeLock) {
            if(listenersMap[identifier.ordinal()] == null) {
                listenersMap[identifier.ordinal()] = new LLinkedList<EventListener>();
            }
        }

        get(identifier).add(listener);
    }

}
