package me.linusdev.discordbotapi.api.objects.channel.thread;


import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
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
public class ThreadMetadata implements Datable {

    public static final String ARCHIVED_KEY = "archived";
    public static final String ARCHIVE_TIMESTAMP_KEY = "archive_timestamp";
    public static final String AUTO_ARCHIVE_DURATION_KEY = "auto_archive_duration";
    public static final String LOCKED_KEY = "locked";
    public static final String INVITABLE_KEY = "invitable";


    private final boolean archived;
    private final ISO8601Timestamp archiveTimestamp;
    private final int autoArchiveDuration;
    private final boolean locked;
    private @Nullable final Boolean invitable;


    /**
     *
     * @param data {@link Data} with required fields
     * @throws InvalidDataException if {@link #ARCHIVED_KEY}, {@link #ARCHIVE_TIMESTAMP_KEY}, {@link #AUTO_ARCHIVE_DURATION_KEY} or {@link #LOCKED_KEY} are missing or null
     */
    @SuppressWarnings("ConstantConditions")
    public ThreadMetadata(@NotNull Data data) throws InvalidDataException {

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
    }

    @Override
    public Data getData() {
        Data data = new Data(4);

        data.add(ARCHIVED_KEY, this.archived);
        data.add(ARCHIVE_TIMESTAMP_KEY, this.archiveTimestamp);
        data.add(AUTO_ARCHIVE_DURATION_KEY, this.autoArchiveDuration);
        data.add(LOCKED_KEY, this.locked);

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
     * a user with the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#MANAGE_THREADS MANAGE_THREADS} permission
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
}
