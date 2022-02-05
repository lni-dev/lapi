package me.linusdev.discordbotapi.api.communication.gateway.events.ready;

import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;

public class LApiReadyEvent extends Event {
    public LApiReadyEvent(@NotNull LApi lApi) {
        super(lApi, null, null);
    }
}
