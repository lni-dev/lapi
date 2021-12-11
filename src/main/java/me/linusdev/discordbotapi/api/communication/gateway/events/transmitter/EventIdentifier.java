package me.linusdev.discordbotapi.api.communication.gateway.events.transmitter;

import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;

public enum EventIdentifier{

    UNKNOWN,

    /**
     * identifier for {@link EventListener#onReady(ReadyEvent)}
     */
    READY,

    /**
     * identifier for {@link EventListener#onMessageCreate(MessageCreateEvent)}
     */
    MESSAGE_CREATE,
    /**
     * identifier for {@link EventListener#onGuildMessageCreate(GuildMessageCreateEvent)}
     */
    GUILD_MESSAGE_CREATE,
    /**
     * identifier for {@link EventListener#onNonGuildMessageCreate(MessageCreateEvent)}
     */
    NON_GUILD_MESSAGE_CREATE,
}
