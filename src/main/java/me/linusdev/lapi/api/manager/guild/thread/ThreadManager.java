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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.events.thread.ThreadListSyncData;
import me.linusdev.lapi.api.communication.gateway.events.thread.ThreadMembersUpdateData;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.interfaces.updatable.Updatable;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ThreadManager extends ThreadPool, Manager {
    void add(Thread<?> thread);

    /**
     * Received when a new Thread is created (then newly_created field exists) or
     * when the current user is added to this (private) thread.
     * @param data thread data
     * @return {@link Thread} contained in this manager
     * @throws InvalidDataException if data parameter is invalid
     */
    @NotNull Update<Thread<?>, Thread<?>> onCreate(@NotNull SOData data) throws InvalidDataException;

    /**
     * Received when a thread is updated. (not when last_message_id changes)
     * @param data {@link Data} to {@link Updatable#updateSelfByData(Data) update}
     * @return {@link ThreadUpdate} or {@code null} if this thread is not cached. (Which is totally valid and not an error!)
     * @throws InvalidDataException if id is missing in data or in {@link Updatable#updateSelfByData(Data)}
     */
    @NotNull ThreadUpdate onUpdate(@NotNull SOData data) throws InvalidDataException;

    /**
     * Received, when a thread relevant to the current user is deleted.
     * The {@link Data} only contains the id, guild_id, parent_id, and type fields.
     * @param data {@link Data} containing just the id, guild_id, parent_id, and type fields.
     * @return {@link Thread} which was removed from this manager or {@code null} if there was no thread with given id.
     * @throws InvalidDataException if id field is missing in given data
     */
    @Nullable Thread<?> onDelete(@NotNull SOData data) throws InvalidDataException;

    /**
     *
     * This will sync all the currently cached threads, with the received {@link ThreadListSyncData}.
     * That process might take some time.
     *
     * @param threadListSyncData {@link ThreadListSyncData} received from the event
     * @return {@link ListUpdate}, but {@link ListUpdate#getOld() old} and {@link ListUpdate#getUpdated() updated} will always be {@code null}
     * @throws InvalidDataException specified by implementation
     */
    @NotNull ListUpdate<Thread<?>> onThreadListSync(@NotNull ThreadListSyncData threadListSyncData) throws InvalidDataException;

    /**
     *
     * @param data retrieved {@link Data}
     * @return {@link ThreadMemberUpdate} containing the new and old {@link ThreadMember} object and the updated {@link Thread}.
     * Or {@code null} if no thread with given id (in data) is cached.
     * @throws InvalidDataException see {@link ThreadMember#fromData(Data)}
     */
    @NotNull ThreadMemberUpdate onThreadMemberUpdate(@NotNull SOData data) throws InvalidDataException;

    /**
     *
     * @param threadMembersUpdateData received from Discord
     * @throws InvalidDataException specified by implementation
     */
    void onThreadMembersUpdate(@NotNull ThreadMembersUpdateData threadMembersUpdateData) throws InvalidDataException;
}
