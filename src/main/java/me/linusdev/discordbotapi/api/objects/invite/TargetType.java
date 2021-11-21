package me.linusdev.discordbotapi.api.objects.invite;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TargetType implements SimpleDatable {
    /**
     * LApi specific
     */
    UNKNOWN(0),

    STREAM(1),

    EMBEDDED_APPLICATION(2),
    ;

    private final int value;

    TargetType(int value){
        this.value = value;
    }

    /**
     *
     * @param value to get {@link TargetType} for
     * @return {@link TargetType} matching given value, or {@link #UNKNOWN} if none matches
     */
    public static @NotNull TargetType fromValue(int value){
        for(TargetType type : TargetType.values()){
            if(type.value == value) return type;
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
