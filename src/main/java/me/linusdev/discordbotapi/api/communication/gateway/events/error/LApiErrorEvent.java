package me.linusdev.discordbotapi.api.communication.gateway.events.error;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This Event occurs, when an Error in {@link me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket GatewayWebSocket's}
 * event handling appears. Errors that cause this event, are by no means {@link Exception Exceptions}, but logical errors instead.
 */
public class LApiErrorEvent extends Event {

     private final @Nullable GatewayEvent inEvent;
     private final @Nullable LApiError error;

    public LApiErrorEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable GatewayEvent inEvent, @Nullable LApiError error) {
        super(lApi, payload, null);
        this.inEvent = inEvent;
        this.error = error;
    }

    /**
     * The event, the error was caused by.
     */
    public @Nullable GatewayEvent getInEvent() {
        return inEvent;
    }

    public @Nullable LApiError getError() {
        return error;
    }
}
