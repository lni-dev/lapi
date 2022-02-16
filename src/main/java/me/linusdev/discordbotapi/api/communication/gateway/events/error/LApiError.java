package me.linusdev.discordbotapi.api.communication.gateway.events.error;

import org.jetbrains.annotations.Nullable;

public class LApiError {

    public enum ErrorCode{
        UNKNOWN_GUILD,
        UNKNOWN_ROLE,
    }

    private final @Nullable ErrorCode code;
    private final @Nullable String message;

    public LApiError(@Nullable ErrorCode code, @Nullable String message){
        this.code = code;
        this.message = message;
    }

    public @Nullable ErrorCode getCode() {
        return code;
    }

    public @Nullable String getMessage() {
        return message;
    }
}
