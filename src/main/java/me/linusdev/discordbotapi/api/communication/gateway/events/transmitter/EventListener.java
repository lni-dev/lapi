package me.linusdev.discordbotapi.api.communication.gateway.events.transmitter;

import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a listener which will listen to events. There is no need to call any super methods.<br><br>
 *
 * TODO how to add a listener to LApi Gateway
 */
public interface EventListener {

    default void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload){}

    default void onReady(@NotNull ReadyEvent event) {}

    default void onMessageCreate(@NotNull MessageCreateEvent event) {}

    default void onNonGuildMessageCreate(@NotNull MessageCreateEvent event){}

    default void onGuildMessageCreate(@NotNull GuildMessageCreateEvent event){}
}

