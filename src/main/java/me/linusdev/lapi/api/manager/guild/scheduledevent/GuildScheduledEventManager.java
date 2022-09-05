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

package me.linusdev.lapi.api.manager.guild.scheduledevent;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventUserAddRemoveData;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.other.LApiImplConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface GuildScheduledEventManager extends Manager, GuildScheduledEventPool {

    /**
     *
     * @param data {@link GuildScheduledEventUserAddRemoveData}
     * @return the {@link GuildScheduledEvent} the user was added to
     */
    @Nullable GuildScheduledEvent onUserAdd(@NotNull GuildScheduledEventUserAddRemoveData data);

    /**
     *
     * @param data {@link GuildScheduledEventUserAddRemoveData}
     * @return the {@link GuildScheduledEvent} the user was removed from
     */
    @Nullable GuildScheduledEvent onUserRemove(@NotNull GuildScheduledEventUserAddRemoveData data);

    void add(GuildScheduledEvent obj);

    /**
     * Adds given data to the current list. If an old entry with the same id exists, it is overwritten.
     *
     * @param data data of {@link GuildScheduledEvent} to add
     * @return added {@link GuildScheduledEvent}
     * @throws InvalidDataException in {@link LApiImplConverter#convert(LApiImpl, Object)}
     */
    @NotNull GuildScheduledEvent onAdd(@NotNull SOData data) throws InvalidDataException;

    /**
     * Updates {@link GuildScheduledEvent} with the same id as given {@link SOData}. If no such {@link GuildScheduledEvent} exists, {@code null} is returned.
     * @param data data of {@link GuildScheduledEvent} to {@link CopyAndUpdatable#updateSelfByData(SOData) updated}
     * @return {@link Update}
     * @throws InvalidDataException in {@link LApiImplConverter#convert(LApiImpl, Object)}
     */
    @Nullable Update<GuildScheduledEvent, GuildScheduledEvent> onUpdate(@NotNull SOData data) throws InvalidDataException;

    /**
     *
     * @param id id of the {@link GuildScheduledEvent} to remove
     * @return {@link GuildScheduledEvent} which was removed, or {@code null} if there was no {@link GuildScheduledEvent} with given id
     */
    @Nullable GuildScheduledEvent onDelete(@NotNull String id);

    /**
     *
     * @param data containing the id key of the {@link GuildScheduledEvent} to remove
     * @return {@link GuildScheduledEvent} which was removed.
     * @throws InvalidDataException if id field is missing
     */
    default @Nullable GuildScheduledEvent onDelete(@NotNull SOData data) throws InvalidDataException {
        String id = (String) data.get(GuildScheduledEvent.ID_KEY);
        if(id == null) throw new InvalidDataException(data, "id field missing in data");
        return onDelete(id);
    }

}
