package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.AbstractEventTransmitter;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.discordbotapi.api.manager.Manager;
import me.linusdev.discordbotapi.api.objects.guild.Guild;
import me.linusdev.discordbotapi.api.objects.guild.GuildAbstract;
import me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild;
import org.jetbrains.annotations.NotNull;

import java.net.http.WebSocket;

public interface GuildManager extends GuildPool, Manager {

    boolean allGuildsReceivedEvent();

    void onReady(@NotNull ReadyEvent event);

    UpdatableGuild onGuildCreate( @NotNull GatewayPayloadAbstract payload) throws InvalidDataException;

    UpdatableGuild onGuildDelete( @NotNull GatewayPayloadAbstract payload) throws InvalidDataException;

    UpdatableGuild onGuildUpdate( @NotNull GatewayPayloadAbstract payload) throws InvalidDataException;

}
