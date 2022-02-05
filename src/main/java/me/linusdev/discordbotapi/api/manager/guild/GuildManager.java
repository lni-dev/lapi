package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.manager.Manager;
import me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild;
import org.jetbrains.annotations.NotNull;


public interface GuildManager extends GuildPool, Manager {

    /**
     * This should return {@code true} once all guilds received either a
     * {@link me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE GUILD_CREATE}
     * or a
     * {@link me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent#GUILD_DELETE GUILD_DELETE}
     * , {@code false} otherwise
     *
     * @return boolean as specified above
     */
    boolean allGuildsReceivedEvent();

    /**
     * Ready event to init guilds as unavailable(Note: they are probably not unavailable, they just always start of as unavailable).
     * If a guild actually is unavailable, it will receive a
     * {@link me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent#GUILD_DELETE GUILD_DELETE} event.
     * If a guild is available, it will receive a
     * {@link me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE GUILD_CREATE} event.
     * <br><br>
     * Note that this event, may also occur randomly, if the gateway connection closes, and it has to
     * {@link GatewayWebSocket#reconnect(boolean) reconnect}. {@link #allGuildsReceivedEvent()} should then return the appropriate
     * boolean, so a single new {@link me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventIdentifier#GUILDS_READY GUILDS_READY}
     * event can occur.
     * @param event the {@link ReadyEvent}
     */
    void onReady(@NotNull ReadyEvent event);

    /**
     *
     * @param payload the payload received from Discord
     * @return The {@link UpdatableGuild}, whether is a new guild for the current user (user joined), whether this guild was unavailable and turned available
     */
    GatewayWebSocket.OnGuildCreateReturn onGuildCreate(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException;

    /**
     * This method must set {@link UpdatableGuild#setRemoved(boolean) UpdateableGuild.setRemoved}, if the current user was removed or left the guild.
     * (This is important for the {@link me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket GatewayWebSocket})
     *
     * @param payload the payload received from Discord
     */
    UpdatableGuild onGuildDelete( @NotNull GatewayPayloadAbstract payload) throws InvalidDataException;

    /**
     * This method should call {@link UpdatableGuild#updateSelfByData(Data)}
     * @param payload the payload received from Discord
     * @return the updated guild
     */
    UpdatableGuild onGuildUpdate( @NotNull GatewayPayloadAbstract payload) throws InvalidDataException;

}
