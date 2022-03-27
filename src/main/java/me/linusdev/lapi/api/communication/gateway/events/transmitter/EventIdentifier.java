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

import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;

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
 * <p>
 *     Almost all events require {@link ConfigFlag#ENABLE_GATEWAY}. This won't be listed below.
 * </p>
 *
 */
public enum EventIdentifier{

    /**
     * LApi specific
     */
    UNKNOWN,


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             READY                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onReady(ReadyEvent)}.
     */
    READY,

    /**
     * identifier for {@link EventListener#onGuildsReady(GuildsReadyEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link ConfigFlag#CACHE_GUILDS}
     *     </li>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILDS_READY,

    /**
     * identifier for {@link EventListener#onLApiReady(LApiReadyEvent)}.
     */
    LAPI_READY,


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             GUILD                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onGuildCreate(GuildCreateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_CREATE,

    /**
     * identifier for {@link EventListener#onGuildDelete(GuildDeleteEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_DELETE,

    /**
     * identifier for {@link EventListener#onGuildUpdate(GuildUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_UPDATE,

    /**
     * identifier for {@link EventListener#onGuildJoined(GuildJoinedEvent)}.
     *
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link ConfigFlag#CACHE_GUILDS}
     *     </li>
     *     <li>
     *         {@link ConfigFlag#ENABLE_GATEWAY}
     *     </li>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_JOINED,

    /**
     * identifier for {@link EventListener#onGuildLeft(GuildLeftEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link ConfigFlag#CACHE_GUILDS}
     *     </li>
     *     <li>
     *         {@link ConfigFlag#ENABLE_GATEWAY}
     *     </li>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_LEFT,

    /**
     * identifier for {@link EventListener#onGuildUnavailable(GuildUnavailableEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_UNAVAILABLE,

    /**
     * identifier for {@link EventListener#onGuildAvailable(GuildAvailableEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_AVAILABLE,

    /**
     * identifier for {@link EventListener#onGuildEmojisUpdate(GuildEmojisUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_EMOJIS_AND_STICKERS}
     *     </li>
     * </ul>
     */
    GUILD_EMOJIS_UPDATE,

    /**
     * identifier for {@link EventListener#onGuildRoleCreate(GuildRoleCreateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_ROLE_CREATE,

    /**
     * identifier for {@link EventListener#onGuildRoleUpdate(GuildRoleUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_ROLE_UPDATE,

    /**
     * identifier for {@link EventListener#onGuildRoleUpdate(GuildRoleUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    GUILD_ROLE_DELETE,


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           MESSAGE                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             OTHER                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onLApiError(LApiErrorEvent)}.
     */
    LAPI_ERROR,
}
