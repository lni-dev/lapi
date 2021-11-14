package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a timestamp often used by discord. Currently, this class only holds the string of the timestamp
 */
public class ISO8601Timestamp implements SimpleDatable {

    private @NotNull String timestamp;

    public ISO8601Timestamp(@NotNull String timestamp){
        this.timestamp = timestamp;
    }

    public static @Nullable ISO8601Timestamp fromString(@Nullable String timestamp){
        if(timestamp == null) return null;
        return new ISO8601Timestamp(timestamp);
    }

    public @NotNull String getTimestamp() {
        return timestamp;
    }

    @Override
    public @NotNull Object simplify() {
        return timestamp;
    }
}
