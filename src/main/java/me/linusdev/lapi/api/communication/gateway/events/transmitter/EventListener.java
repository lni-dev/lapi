/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.communication.gateway.events.transmitter;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.sticker.GuildStickersUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     This listener can listen to events if added to the {@link AbstractEventTransmitter EventTransmitter}.
 * </p>
 * <p>
 *     Even though all methods are {@code default} they do not have any code in them, and you should not call any
 *     super methods. The methods are {@code default} simply for the reason, that you wont need all of them every time
 * </p>
 * <br>
 * <h2 style="padding:0;margin:0;">How to add a listener to the event transmitter?</h2>
 * <p style="padding:0;margin:0;padding-top:3px">
 *      After you {@link LApi have created your LApi instance}, you can use {@link LApi#getEventTransmitter()} to get
 *      the event transmitter. Then you can simply add a listener by calling {@link AbstractEventTransmitter#addListener(EventListener)}.
 * </p>
 * <p>
 *     If you want to know more about how to add specified listeners, see
 *     {@link AbstractEventTransmitter#addSpecifiedListener(EventListener, EventIdentifier...) here}. Specified listeners will
       only listen to specified events or sub-events
 * </p>
 *
 *
 */
public interface EventListener {

    default void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload){}



    default void onReady(@NotNull ReadyEvent event) {}

    /**
     * This event will be triggered, when data for all guilds, the current user is a member of,
     * has been retrieved.<br>
     * This can also happen randomly, if the gateway
     * {@link me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket#reconnect(boolean) reconnects}.
     */
    default void onGuildsReady(@NotNull GuildsReadyEvent event) {}

    /**
     * TODO: when does it happen / make it happen<br>
     * This event will only happen once.
     */
    default void onLApiReady(@NotNull LApiReadyEvent event) {}

    default void onGuildCreate(@NotNull GuildCreateEvent event) {}

    default void onGuildDelete(@NotNull GuildDeleteEvent event) {}

    default void onGuildUpdate(@NotNull GuildUpdateEvent event) {}

    /**
     * Triggered, when the current user joined a guild
     */
    default void onGuildJoined(@NotNull GuildJoinedEvent event) {}

    /**
     * Triggered, when the current user left a guild
     */
    default void onGuildLeft(@NotNull GuildLeftEvent event) {}

    /**
     * If a guild becomes unavailable (or is already unavailable when the gateway initializes) this event will trigger.
     */
    default void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {}

    /**
     * Triggered when an unavailable guild (due to an outage) becomes available again
     */
    default void onGuildAvailable(@NotNull GuildAvailableEvent event) {}

    default void onGuildEmojisUpdate(@NotNull GuildEmojisUpdateEvent event) {}

    default void onGuildStickersUpdate(@NotNull GuildStickersUpdateEvent event) {}

    default void onGuildRoleCreate(@NotNull GuildRoleCreateEvent event) {}

    default void onGuildRoleUpdate(@NotNull GuildRoleUpdateEvent event) {}

    default void onGuildRoleDelete(@NotNull GuildRoleDeleteEvent event) {}

    default void onMessageCreate(@NotNull MessageCreateEvent event) {}

    default void onNonGuildMessageCreate(@NotNull MessageCreateEvent event) {}

    default void onGuildMessageCreate(@NotNull GuildMessageCreateEvent event) {}

    default void onInteractionCreate(@NotNull InteractionCreateEvent event) {}

    default void onLApiError(@NotNull LApiErrorEvent event) { }

}

