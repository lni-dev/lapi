package me.linusdev.discordbotapi.api.communication.gateway.events.transmitter;

import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;

/**
 * <p>
 *     This is a list of all events you can listen to.
 * </p>
 * <p>
 *   The identifiers are only required if you want to add a
 *   {@link EventTransmitter#addSpecifiedListener(EventListener, EventIdentifier...) specified listener}.
 * </p>
 * <p>
 *     If you want to know more about how to add a listener, see {@link EventListener}.
 * </p>
 * <p>
 *     sub-events are events, which we don't receive from Discord directly, but are the result of splitting other events.
 *     All this information is probably useless to you, you can just listen to the events using a {@link EventListener}.
 *     It is just documented for the sake of being documented.
 * </p>
 *
 */
public enum EventIdentifier{

    /**
     * LApi specific
     */
    UNKNOWN,



    /**
     * identifier for {@link EventListener#onReady(ReadyEvent)}.
     */
    READY,



    /**
     * identifier for {@link EventListener#onMessageCreate(MessageCreateEvent)}.
     */
    MESSAGE_CREATE,

    /**
     * identifier for {@link EventListener#onGuildMessageCreate(GuildMessageCreateEvent)}.<br>
     * sub-event of {@link #MESSAGE_CREATE}.
     */
    GUILD_MESSAGE_CREATE,

    /**
     * identifier for {@link EventListener#onNonGuildMessageCreate(MessageCreateEvent)}.<br>
     * sub-event of {@link #MESSAGE_CREATE}.
     */
    NON_GUILD_MESSAGE_CREATE,
}
