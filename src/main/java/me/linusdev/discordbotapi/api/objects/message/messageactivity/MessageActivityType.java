package me.linusdev.discordbotapi.api.objects.message.messageactivity;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-activity-types" target="">Message Activity Types</a>
 */
public enum MessageActivityType implements SimpleDatable {
    UNKNOWN(0),
    JOIN(1),
    SPECTATE(2),
    LISTEN(3),
    JOIN_REQUEST(5),
    ;

    private final int value;

    MessageActivityType(int value){
        this.value = value;
    }

    /**
     * @return {@link MessageActivityType} with given value or {@link #UNKNOWN} if no such type exists
     */
    public static @NotNull MessageActivityType fromValue(int value){
        for(MessageActivityType type : MessageActivityType.values()){
            if(type.value == value) return type;
        }

        return UNKNOWN;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
