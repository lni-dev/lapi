package me.linusdev.discordbotapi.api.objects.guild.scheduledevent;

import me.linusdev.data.SimpleDatable;

/**
 * <p>
 *     Once status is set to {@link #COMPLETED} or {@link #CANCELED}, the status can no longer be updated.
 * </p>
 * <p>
 * <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-valid-guild-scheduled-event-status-transitions" target="_top">Valid GuildImpl Scheduled Event Status Transitions</a>:<br>
 *
 * SCHEDULED --> ACTIVE<br>
 *
 * ACTIVE --------> COMPLETED<br>
 *
 * SCHEDULED --> CANCELED<br>
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-status" target="_top">GuildImpl Scheduled Event Status</a>
 */
public enum Status implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(0),

    SCHEDULED(1),
    ACTIVE(2),
    COMPLETED(3),
    CANCELED(4),
    ;

    private final int value;

    Status(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link Status} matching given value or {@link #UNKNOWN} if none matches
     */
    public static Status fromValue(int value){
        for(Status status : Status.values()){
            if(status.value == value) return status;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
