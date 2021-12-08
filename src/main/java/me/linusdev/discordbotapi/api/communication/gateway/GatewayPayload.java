package me.linusdev.discordbotapi.api.communication.gateway;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#payloads-gateway-payload-structure" target="_top">Gateway payload strcuture</a>
 */
public class GatewayPayload implements Datable {

    public static final String OPCODE_KEY = "op";
    public static final String DATA_KEY = "d";
    public static final String SEQUENCE_KEY = "s";
    public static final String TYPE_KEY = "t";

    private final @NotNull GatewayOpcode opcode;
    private final @Nullable Object data;

    /**
     * -1 can be considered {@code null}
     */
    private final int sequence;
    private final @Nullable String type;

    /**
     *
     * @param opcode opcode for the payload
     * @param data event data
     * @param sequence sequence number, used for resuming sessions and heartbeats. -1 can be considered null
     * @param type the event name for this payload
     */
    public GatewayPayload(@NotNull GatewayOpcode opcode, @Nullable Object data, int sequence, @Nullable String type) {
        this.opcode = opcode;
        this.data = data;
        this.sequence = sequence;
        this.type = type;
    }

    /**
     * Creates a Heartbeat GatewayPayload with opcode {@value Gateway#HEARTBEAT_OPCODE} and given sequence.
     * @return {@link GatewayPayload}
     * @see <a href="https://discord.com/developers/docs/topics/gateway#heartbeating" target="_top">Heartbeating</a>
     */
    public static @NotNull GatewayPayload newHeartbeat(int sequence){
        return new GatewayPayload(GatewayOpcode.HEARTBEAT, null, sequence, null);
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link GatewayPayload}
     * @throws InvalidDataException if {@link #OPCODE_KEY} is missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable GatewayPayload fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;
        Number op = (Number) data.get(OPCODE_KEY);
        Object d = data.get(DATA_KEY);
        Number s = (Number) data.get(SEQUENCE_KEY);
        String t = (String) data.get(TYPE_KEY);

        if(op == null) {
            InvalidDataException.throwException(data, null, GatewayPayload.class, new Object[]{op}, new String[]{OPCODE_KEY});
        }

        return new GatewayPayload(
                op == null ? GatewayOpcode.UNKNOWN : GatewayOpcode.fromValue(op.intValue()), d,
                s == null ? -1 : s.intValue(), t
        );
    }

    /**
     * opcode for the payload
     */
    public GatewayOpcode getOpcode() {
        return opcode;
    }

    /**
     * event data
     */
    public @Nullable Object getPayloadData() {
        return data;
    }

    /**
     * sequence number, used for resuming sessions and heartbeats
     * @return -1 if no sequence is given
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * the event name for this payload
     */
    public @Nullable String getType() {
        return type;
    }

    @Override
    public Data getData() {
        Data data = new Data(4);

        data.add(OPCODE_KEY, opcode);
        data.add(DATA_KEY, this.data);
        data.add(SEQUENCE_KEY, sequence == -1 ? null : sequence);
        data.add(TYPE_KEY, type);

        return data;
    }
}
