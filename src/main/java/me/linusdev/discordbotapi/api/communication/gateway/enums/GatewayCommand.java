package me.linusdev.discordbotapi.api.communication.gateway.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-commands" target="_top">Gateway Commands</a>
 */
public enum GatewayCommand implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(GatewayOpcode.UNKNOWN, "UNKNOWN"),

    /**
     * triggers the initial handshake with the gateway
     */
    IDENTIFY(GatewayOpcode.IDENTIFY, "IDENTIFY"),

    /**
     * resumes a dropped gateway connection
     */
    RESUME(GatewayOpcode.RESUME, "RESUME"),

    /**
     * maintains an active gateway connection
     */
    HEARTBEAT(GatewayOpcode.HEARTBEAT, "HEARTBEAT"),






    /**
     * requests members for a guild
     */
    REQUEST_GUILD_MEMBERS(GatewayOpcode.REQUEST_GUILD_MEMBERS, "REQUEST_GUILD_MEMBERS"),

    /**
     * joins, moves, or disconnects the client from a voice channel
     */
    UPDATE_VOICE_STATE(GatewayOpcode.VOICE_STATE_UPDATE, "UPDATE_VOICE_STATE"),

    /**
     * updates a client's presence
     */
    UPDATE_PRESENCE(GatewayOpcode.PRESENCE_UPDATE, "UPDATE_PRESENCE"),

    ;

    private final @NotNull GatewayOpcode opcode;
    private final @NotNull String name;

    GatewayCommand(@NotNull GatewayOpcode opcode, @NotNull String value) {
        this.opcode = opcode;
        this.name = value;
    }

    /**
     *
     * @param value event-string
     * @return {@link GatewayCommand} matching given string or {@link #UNKNOWN} if none matches. Or {@code null} if given value is {@code null}
     */
    public static @Nullable GatewayCommand fromString(@Nullable String value){
        if(value == null) return null;
        for(GatewayCommand event : GatewayCommand.values()){
            if(event.name.equalsIgnoreCase(value)){
                return event;
            }
        }

        return UNKNOWN;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull GatewayOpcode getOpcode() {
        return opcode;
    }

    @Override
    public Object simplify() {
        return name;
    }
}
