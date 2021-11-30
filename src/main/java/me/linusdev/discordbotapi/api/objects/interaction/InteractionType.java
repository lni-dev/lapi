package me.linusdev.discordbotapi.api.objects.interaction;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-type" target="_top">Interaction Type</a>
 */
public enum InteractionType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(0),

    PING(1),
    APPLICATION_COMMAND(2),
    MESSAGE_COMPONENT(3),
    APPLICATION_COMMAND_AUTOCOMPLETE(4),

    ;

    private final int value;

    InteractionType(int value) {
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link InteractionType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull InteractionType fromValue(int value){
        for(InteractionType type : InteractionType.values()){
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
