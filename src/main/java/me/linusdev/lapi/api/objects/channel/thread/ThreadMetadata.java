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

package me.linusdev.lapi.api.objects.channel.thread;


import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * <p>
 *     The thread metadata object contains a number of thread-specific channel fields that are not needed by other channel types.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#thread-metadata-object" target="_top">Thread Metadata Object</a>
 */
public class ThreadMetadata implements Copyable<ThreadMetadata>, Datable {

    public static final String ARCHIVED_KEY = "archived";
    public static final String ARCHIVE_TIMESTAMP_KEY = "archive_timestamp";
    public static final String AUTO_ARCHIVE_DURATION_KEY = "auto_archive_duration";
    public static final String LOCKED_KEY = "locked";
    public static final String INVITABLE_KEY = "invitable";
    public static final String CREATE_TIMESTAMP_KEY = "create_timestamp";


    private final boolean archived;
    private final ISO8601Timestamp archiveTimestamp;
    private final int autoArchiveDuration;
    private final boolean locked;
    private @Nullable final Boolean invitable;
    private final @Nullable ISO8601Timestamp createTimestamp;


    /**
     *
     * @param data {@link SOData} with required fields
     * @throws InvalidDataException if {@link #ARCHIVED_KEY}, {@link #ARCHIVE_TIMESTAMP_KEY}, {@link #AUTO_ARCHIVE_DURATION_KEY} or {@link #LOCKED_KEY} are missing or null
     */
    @SuppressWarnings("ConstantConditions")
    public ThreadMetadata(@NotNull SOData data) throws InvalidDataException {

        Boolean archived = (Boolean) data.get(ARCHIVED_KEY);
        String archiveTimestamp = (String) data.get(ARCHIVE_TIMESTAMP_KEY);
        Number autoArchiveDuration = (Number) data.get(AUTO_ARCHIVE_DURATION_KEY);
        Boolean locked = (Boolean) data.get(LOCKED_KEY);
        Boolean invitable = (Boolean) data.get(INVITABLE_KEY);

        if(archived == null || archiveTimestamp == null || autoArchiveDuration == null || locked == null) {
            InvalidDataException.throwException(data, null, ThreadMetadata.class,
                    new Object[]{archived, archiveTimestamp, autoArchiveDuration, locked},
                    new String[]{ARCHIVED_KEY, ARCHIVE_TIMESTAMP_KEY, AUTO_ARCHIVE_DURATION_KEY, LOCKED_KEY});
        }


        this.archived = archived;
        this.archiveTimestamp = ISO8601Timestamp.fromString(archiveTimestamp);
        this.autoArchiveDuration = autoArchiveDuration.intValue();
        this.locked = locked;
        this.invitable = invitable;
        this.createTimestamp = data.getAndConvert(CREATE_TIMESTAMP_KEY, ISO8601Timestamp::fromString);
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(ARCHIVED_KEY, this.archived);
        data.add(ARCHIVE_TIMESTAMP_KEY, this.archiveTimestamp);
        data.add(AUTO_ARCHIVE_DURATION_KEY, this.autoArchiveDuration);
        data.add(LOCKED_KEY, this.locked);
        data.addIfNotNull(INVITABLE_KEY, invitable);
        data.addIfNotNull(CREATE_TIMESTAMP_KEY, createTimestamp);

        return data;
    }

    /**
     * whether this Thread is archived (true) or not (false)
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * timestamp when the thread's archive status was last changed, used for calculating recent activity
     */
    public ISO8601Timestamp getArchiveTimestamp() {
        return archiveTimestamp;
    }

    /**
     * The duration, in minutes, a thread must be inactive to be archived
     */
    public int getAutoArchiveDuration() {
        return autoArchiveDuration;
    }

    /**
     * Threads that have locked set to true can only be unarchived by
     * a user with the {@link Permission#MANAGE_THREADS MANAGE_THREADS} permission
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * whether non-moderators can add other non-moderators to a thread; only available on private threads
     */
    public @Nullable Boolean getInvitable() {
        return invitable;
    }

    /**
     * Timestamp when the thread was created; only populated for threads created after 2022-01-09.
     */
    public @Nullable ISO8601Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     *
     * @return this, because everything in this class is final
     */
    @Override
    public @NotNull ThreadMetadata copy() {
        return this;
    }
}
