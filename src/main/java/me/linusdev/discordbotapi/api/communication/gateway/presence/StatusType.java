package me.linusdev.discordbotapi.api.communication.gateway.presence;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#update-presence-status-types" target="_top">Status Types</a>
 */
public enum StatusType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN("unknown"),

    /**
     * Online
     */
    ONLINE("online"),

    /**
     * Do Not Disturb
     */
    DND("dnd"),

    /**
     * AFK
     */
    IDLE("idle"),

    /**
     * Invisible and shown as offline
     */
    INVISIBLE("invisible"),

    /**
     * Offline
     */
    OFFLINE("offline"),
    ;

    private final String value;

    StatusType(String value) {
        this.value = value;
    }

    /**
     *
     * @param value {@link String}
     * @return {@link StatusType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull StatusType fromValue(String value){
        for(StatusType type : StatusType.values()){
            if(type.value.equalsIgnoreCase(value)) return type;
        }

        return UNKNOWN;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
