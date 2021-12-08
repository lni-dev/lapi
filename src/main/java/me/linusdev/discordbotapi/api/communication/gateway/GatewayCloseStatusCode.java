package me.linusdev.discordbotapi.api.communication.gateway;

import me.linusdev.data.SimpleDatable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/opcodes-and-status-codes#gateway-gateway-close-event-codes" target="_top">Gateway Close Event Codes</a>
 */
public enum GatewayCloseStatusCode implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-4000),

    /**
     * We're not sure what went wrong. Try reconnecting?
     */
    UNKNOWN_ERROR(4000),

    /**
     * You sent an invalid {@link GatewayOpcode Gateway opcode} or an invalid payload for an opcode. Don't do that!
     */
    UNKNOWN_OPCODE(4001),

    /**
     * You sent an invalid {@link GatewayPayload payload} to us. Don't do that!
     */
    DECODE_ERROR(4002),

    /**
     * You sent us a payload prior to identifying.
     */
    NOT_AUTHENTICATED(4003),

    /**
     * The account token sent with your {@link me.linusdev.discordbotapi.api.communication.gateway.identify.Identify identify} payload is incorrect.
     */
    AUTHENTICATION_FAILED(4004),

    /**
     * You sent more than one identify payload. Don't do that!
     */
    ALREADY_AUTHENTICATED(4005),

    /**
     * The sequence sent when resuming the session was invalid. Reconnect and start a new session.
     */
    INVALID_SEQUENCE(4007),

    /**
     * Woah nelly! You're sending payloads to us too quickly. Slow it down! You will be disconnected on receiving this.
     */
    RATE_LIMITED(4008),

    /**
     * Your session timed out. Reconnect and start a new one.
     */
    SESSION_TIMED_OUT(4009),

    /**
     * You sent us an invalid shard when identifying.
     */
    INVALID_SHARD(4010),

    /**
     * The session would have handled too many guilds - you are required to shard your connection in order to connect.
     */
    SHARDING_REQUIRED(4011),

    /**
     * You sent an invalid version for the gateway.
     */
    INVALID_API_VERSION(4012),

    /**
     * You sent an invalid {@link GatewayIntent intent} for a Gateway Intent. You may have incorrectly calculated the bitwise value.
     */
    INVALID_INTENTS(4013),

    /**
     * You sent a disallowed {@link GatewayIntent intent} for a Gateway Intent.
     * You may have tried to specify an intent that you <a href="https://discord.com/developers/docs/topics/gateway#privileged-intents" target="_top">have not enabled or are not approved for.</a>
     */
    DISALLOWED_INTENTS(4014),
    ;

    private final int code;

    GatewayCloseStatusCode(int code) {
        this.code = code;
    }

    /**
     *
     * @param c int
     * @return {@link GatewayCloseStatusCode} matching given int or {@link #UNKNOWN} if none matches
     */
    public static GatewayCloseStatusCode fromInt(int c){
        for(GatewayCloseStatusCode code : GatewayCloseStatusCode.values()){
            if(code.code == c) return code;
        }

        return UNKNOWN;
    }

    public int getCode() {
        return code;
    }

    @Override
    public Object simplify() {
        return code;
    }
}
