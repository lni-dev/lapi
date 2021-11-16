package me.linusdev.discordbotapi.api.objects.user.connection;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/user#connection-object-visibility-types" target="_top">Visibility Types</a>
 */
public enum VisibilityType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * invisible to everyone except the user themselves
     */
    NONE(0),

    /**
     * visible to everyone
     */
    EVERYONE(1),
    ;

    private final int value;

    VisibilityType(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link VisibilityType} matching to given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull VisibilityType fromValue(int value){
        for(VisibilityType type : VisibilityType.values()){
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
