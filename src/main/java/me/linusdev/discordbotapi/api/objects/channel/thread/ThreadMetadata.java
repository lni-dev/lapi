package me.linusdev.discordbotapi.api.objects.channel.thread;


import me.linusdev.data.Data;
import me.linusdev.data.Datable;

public class ThreadMetadata implements Datable {

    public static final String ARCHIVED_KEY = "archived";
    public static final String ARCHIVE_TIMESTAMP_KEY = "archive_timestamp";
    public static final String AUTO_ARCHIVE_DURATION_KEY = "auto_archive_duration";
    public static final String LOCKED_KEY = "locked";


    private boolean archived;
    private String archiveTimestamp;
    private int autoArchiveDuration;
    private boolean locked;


    public ThreadMetadata(Data data){
        this.archived = (boolean) data.getOrDefault(ARCHIVED_KEY, false);
        this.archiveTimestamp = (String) data.get(ARCHIVE_TIMESTAMP_KEY);
        this.autoArchiveDuration = ((Number) data.getOrDefault(AUTO_ARCHIVE_DURATION_KEY, 0)).intValue();
        this.locked = (boolean) data.getOrDefault(LOCKED_KEY, false);
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
     * TODO: What is this exactly?
     */
    public String getArchiveTimestamp() {
        return archiveTimestamp;
    }

    /**
     * The duration, in minutes, a thread must be inactive to be archived
     */
    public int getAutoArchiveDuration() {
        return autoArchiveDuration;
    }

    /**
     * Threads that have locked set to true can only be unarchived by a user with the MANAGE_THREADS permission todo add @link to permission
     */
    public boolean isLocked() {
        return locked;
    }
}
