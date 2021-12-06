package me.linusdev.discordbotapi.api.communication.gateway.activity;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-timestamps" target="_top">Activity Timestamps</a>
 */
public class ActivityTimestamps implements Datable {

    public static final String START_KEY = "start";
    public static final String END_KEY = "end";

    private final @Nullable Long start;
    private final @Nullable Long end;

    /**
     *
     * @param start unix time (in milliseconds) of when the activity started
     * @param end unix time (in milliseconds) of when the activity ends
     */
    public ActivityTimestamps(@Nullable Long start, @Nullable Long end) {
        this.start = start;
        this.end = end;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link ActivityTimestamps}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ActivityTimestamps fromData(@Nullable Data data){
        if(data == null) return null;

        Number start = (Number) data.get(START_KEY);
        Number end = (Number) data.get(END_KEY);

        return new ActivityTimestamps(start == null ? null : start.longValue(),
                end == null ? null : end.longValue());
    }


    /**
     * unix time (in milliseconds) of when the activity started
     */
    public @Nullable Long getStart() {
        return start;
    }

    /**
     * unix time (in milliseconds) of when the activity ends
     */
    public @Nullable Long getEnd() {
        return end;
    }

    @Override
    public Data getData() {
        Data data = new Data(2);

        data.addIfNotNull(START_KEY, start);
        data.addIfNotNull(END_KEY, end);

        return data;
    }
}
