package me.linusdev.discordbotapi.api.objects.message.component;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#component-object-component-types" target="_top">ComponentType</a>
 */
public enum ComponentType implements SimpleDatable {

    /**
     * LApi specific, not in Discord!
     */
    UNKNOWN(0),

    /**
     * A container for other components
     */
    ACTION_ROW(1),

    /**
     * A button object
     */
    BUTTON(2),

    /**
     * A select menu for picking from choices
     */
    SELECT_MENU(3),
    ;

    private final int value;

    ComponentType(int value){
        this.value = value;
    }

    /**
     *
     * @param value to get corresponding {@link ComponentType}
     * @return {@link ComponentType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ComponentType fromValue(int value){
        for(ComponentType type : ComponentType.values()){
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
