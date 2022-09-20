/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.lapi.api.communication.gateway.events.message.GuildMessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageCreateEvent;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AbstractEventTransmitter extends HasLApi {

    /**
     * Adds a listener, which will listen to all events.
     *
     * @param listener the {@link EventListener} to add
     */
    void addListener(@NotNull EventListener listener);

    /**
     * This will not remove {@link #addSpecifiedListener(EventListener, EventIdentifier...) specified listener}!
     * @param listener the {@link EventListener} to remove
     * @return {@code true} if the listener was removed.
     */
    boolean removeListener(@NotNull EventListener listener);

    /**
     * Adds a {@link AnyEventListener}, which will listen to all events expect {@link EventIdentifier#UNKNOWN UNKNOWN} events
     * @param listener {@link AnyEventListener} to add
     */
    void addAnyEventListener(@NotNull AnyEventListener listener);

    /**
     * Removes given {@link AnyEventListener}.
     * @param listener {@link AnyEventListener} to remove
     */
    void removeAnyEventListener(@NotNull AnyEventListener listener);

    /**
     *
     * Adding a {@link EventListener} as specified listener means, that the listener's methods will only listen to
     * events with the specified {@link EventIdentifier}.<br><br>
     *
     * For Example if you add a listener, which overwrites {@link EventListener#onMessageCreate(LApi, MessageCreateEvent)} and
     * add it with the specification {@link EventIdentifier#READY READY}, your listener will have no effect.<br>
     * But if you add this listener with the specification {@link EventIdentifier#MESSAGE_CREATE MESSAGE_CREATE}, the
     * {@link EventListener#onMessageCreate(LApi, MessageCreateEvent) onMessageCreate(MessageCreateEvent)} method of your listener will be called, when a new message
     * was created.<br><br>
     *
     * Note that sub-event-methods of an event are not called if you add the {@link EventIdentifier} for the (super-)event. For Example:<br>
     * If you add a listener with the specification {@link EventIdentifier#MESSAGE_CREATE MESSAGE_CREATE},
     * the {@link EventListener#onGuildMessageCreate(LApi, GuildMessageCreateEvent) onGuildMessageCreate(GuildMessageCreateEvent)}
     * method of your listener will never be called. If you want that method to be called, you will have to add
     * {@link EventIdentifier#GUILD_MESSAGE_CREATE GUILD_MESSAGE_CREATE} instead. You can also add both if you wish.
     *
     * @param listener the listener
     * @param specifications to which Events the listener shall listen to
     */
    void addSpecifiedListener(@NotNull EventListener listener, @NotNull EventIdentifier... specifications);

    /**
     *
     * @param listener the listener to remove
     * @param specifications to which Events the listener shall not listen to anymore
     * @return true if all {@link List#remove(Object) remove} calls returned true
     */
    boolean removeSpecifiedListener(@NotNull EventListener listener, @NotNull EventIdentifier... specifications);
}
