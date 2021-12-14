package me.linusdev.discordbotapi.api.communication.gateway.presence;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.activity.Activity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * <p>
 *     Sent by the client to indicate a presence or status update.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#update-presence-gateway-presence-update-structure" target="_top">Gateway Presence Update Structure</a>
 */
public class PresenceUpdate implements Datable {

    public static final String SINCE_KEY = "since";
    public static final String ACTIVITIES_KEY = "activities";
    public static final String STATUS_KEY = "status";
    public static final String AFK_KEY = "afk";

    private final @Nullable Long since;
    private final @NotNull Activity[] activities;
    private final @NotNull StatusType status;
    private final @NotNull Boolean afk;

    /**
     *
     * @param since unix time (in milliseconds) of when the client went idle, or null if the client is not idle
     * @param activities the user's activities
     * @param status the user's new {@link StatusType status}
     * @param afk whether or not the client is afk
     */
    public PresenceUpdate(@Nullable Long since, @NotNull Activity[] activities, @NotNull StatusType status, @NotNull Boolean afk) {
        this.since = since;
        this.activities = activities;
        this.status = status;
        this.afk = afk;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link PresenceUpdate}
     * @throws InvalidDataException if {@link #ACTIVITIES_KEY}, {@link #STATUS_KEY} or {@link #AFK_KEY} is missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable PresenceUpdate fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        Number since = (Number) data.get(SINCE_KEY);
        ArrayList<Activity> activities = data.getAndConvertArrayList(ACTIVITIES_KEY,
                (ExceptionConverter<Data, Activity, InvalidDataException>) Activity::fromData);
        String status = (String) data.get(STATUS_KEY);
        Boolean afk = (Boolean) data.get(AFK_KEY);

        if(activities == null || status == null || afk == null){
            InvalidDataException.throwException(data, null, PresenceUpdate.class,
                    new Object[]{activities, status, afk},
                    new String[]{ACTIVITIES_KEY, STATUS_KEY, AFK_KEY});
        }

        //noinspection ConstantConditions
        return new PresenceUpdate(since == null ? null : since.longValue(),
                activities.toArray(new Activity[0]),
                StatusType.fromValue(status),
                afk);
    }

    /**
     * unix time (in milliseconds) of when the client went idle, or null if the client is not idle
     */
    public @Nullable Integer getSince() {
        return since;
    }

    /**
     * the user's activities
     */
    public @NotNull Activity[] getActivities() {
        return activities;
    }

    /**
     * the user's new status
     */
    public @NotNull StatusType getStatus() {
        return status;
    }

    /**
     * whether or not the client is afk
     */
    public @NotNull Boolean getAfk() {
        return afk;
    }

    @Override
    public Data getData() {
        Data data = new Data(4);

        data.add(SINCE_KEY, since);
        data.add(ACTIVITIES_KEY, activities);
        data.add(STATUS_KEY, status);
        data.add(AFK_KEY, afk);

        return data;
    }
}
