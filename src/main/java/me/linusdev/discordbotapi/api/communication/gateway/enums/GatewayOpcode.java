package me.linusdev.discordbotapi.api.communication.gateway.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/opcodes-and-status-codes#gateway-gateway-opcodes" target="_top">Gateway Opcodes</a>
 */
public enum GatewayOpcode implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * An event was dispatched.
     */
    DISPATCH(0),

    /**
     * Fired periodically by the client to keep the connection alive.
     */
    HEARTBEAT(1),

    /**
     * Starts a new session during the initial handshake.
     */
    IDENTIFY(2),

    /**
     * Update the client's presence.
     */
    PRESENCE_UPDATE(3),

    /**
     * Used to join/leave or move between voice channels.
     */
    VOICE_STATE_UPDATE(4),

    /**
     * Resume a previous session that was disconnected.
     */
    RESUME(6),

    /**
     * You should attempt to reconnect and resume immediately.
     */
    RECONNECT(7),

    /**
     * Request information about offline guild members in a large guild.
     */
    REQUEST_GUILD_MEMBERS(8),

    /**
     * The session has been invalidated. You should reconnect and identify/resume accordingly.
     */
    INVALID_SESSION(9),

    /**
     * Sent immediately after connecting, contains the heartbeat_interval to use.
     */
    HELLO(10),

    /**
     * Sent in response to receiving a heartbeat to acknowledge that it has been received.
     */
    HEARTBEAT_ACK(11),

    ;

    private final int opcode;

    GatewayOpcode(int opcode) {
        this.opcode = opcode;
    }

    /**
     *
     * @param opcode int
     * @return {@link GatewayOpcode} matching given int or {@link #UNKNOWN} if none matching
     */
    public static @NotNull GatewayOpcode fromValue(int opcode){
        for(GatewayOpcode o : GatewayOpcode.values()){
            if(o.opcode == opcode) return o;
        }

        return UNKNOWN;
    }

    /**
     *
     * @return the opcode int value
     */
    public int getOpcode() {
        return opcode;
    }

    @Override
    public Object simplify() {
        return opcode;
    }
}
