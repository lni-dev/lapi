package me.linusdev.discordbotapi.api.communication.gateway.abstracts;

import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayOpcode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see me.linusdev.discordbotapi.api.communication.gateway.GatewayPayload GatewayPayload Implementation
 */
public interface GatewayPayloadAbstract {

    /**
     * opcode for the payload
     */
    @NotNull GatewayOpcode getOpcode();

    /**
     * event data
     */
    @Nullable Object getPayloadData();

    /**
     * sequence number, used for resuming sessions and heartbeats
     */
    @Nullable Long getSequence();

    /**
     * the event name for this payload
     */
    @Nullable GatewayEvent getType();

    /**
     * The json for this payload. Should not contain {@link #get_trace()}<br>
     * This is used when sending this payload to discord.
     * @return json string of this payload
     */
    @Nullable String toJsonString();


    @Nullable String[] get_trace();
}
