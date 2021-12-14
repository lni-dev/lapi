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
 * <p>
 *     This listener can listen to events if added to the {@link EventTransmitter}.
 * </p>
 * <p>
 *     Even though all methods are {@code default} they do not have any code in them, and you should not call any
 *     super methods. The methods are {@code default} simply for the reason, that you wont need all of them every time
 * </p>
 * <br>
 * <h2 style="padding:0;margin:0;">How to add a listener to the event transmitter?</h2>
 * <p style="padding:0;margin:0;padding-top:3px">
 *      After you {@link LApi have created your LApi instance}, you can use {@link LApi#getEventTransmitter()} to get
 *      the event transmitter. Then you can simply add a listener by calling {@link EventTransmitter#addListener(EventListener)}.
 * </p>
 * <p>
 *     If you want to know more about how to add specified listeners, see
 *     {@link EventTransmitter#addSpecifiedListener(EventListener, EventIdentifier...) here}. Specified listeners will
       only listen to specified events or sub-events
 * </p>
 *
 *
 */
public interface EventListener {

    default void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload){}

    default void onReady(@NotNull ReadyEvent event) {}

    default void onMessageCreate(@NotNull MessageCreateEvent event) {}

    default void onNonGuildMessageCreate(@NotNull MessageCreateEvent event){}

    default void onGuildMessageCreate(@NotNull GuildMessageCreateEvent event){}
}

