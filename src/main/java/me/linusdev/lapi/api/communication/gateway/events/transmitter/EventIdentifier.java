/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.lapi.api.cache.CacheReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelPinsUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.ban.GuildBanEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.integration.GuildIntegrationsUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberAddEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberRemoveEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventUserEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.sticker.GuildStickersUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.invite.InviteCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.invite.InviteDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.*;
import me.linusdev.lapi.api.communication.gateway.events.message.reaction.MessageReactionEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.reaction.MessageReactionRemoveAllEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.reaction.MessageReactionRemoveEmojiEvent;
import me.linusdev.lapi.api.communication.gateway.events.presence.PresenceUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.resumed.ResumedEvent;
import me.linusdev.lapi.api.communication.gateway.events.stage.StageInstanceEvent;
import me.linusdev.lapi.api.communication.gateway.events.thread.*;
import me.linusdev.lapi.api.communication.gateway.events.typing.TypingStartEvent;
import me.linusdev.lapi.api.communication.gateway.events.user.UserUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.voice.VoiceServerUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.voice.VoiceStateUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.webhooks.WebhooksUpdateEvent;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManager;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManagerReadyEvent;

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
 * <p>
 *
 * <a style="margin-bottom:0">Events that do not require {@link ConfigFlag#ENABLE_GATEWAY} are:</a>
 * <ul style="margin-top:0">
 *     <li>
 *         {@link #LAPI_READY}
 *     </li>
 *     <li>
 *         {@link #VOICE_REGION_MANAGER_READY}
 *     </li>
 *     <li>
 *         {@link #CACHE_READY}
 *     </li>
 * </ul>
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
     * identifier for {@link EventListener#onReady(LApi, ReadyEvent)}.
     */
    READY,

    /**
     * identifier for {@link EventListener#onGuildsReady(LApi, GuildsReadyEvent)}.
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
     * identifier for {@link EventListener#onLApiReady(LApi, LApiReadyEvent)}.
     * <br><br>
     * <p>
     *     This event will only happen once after all of the following events were triggered:
     *     {@link #READY}, {@link #GUILDS_READY}, {@link #VOICE_REGION_MANAGER_READY}. If any of these events are disabled
     *     in the {@link ConfigBuilder#setFlags(long) config}, they will be ignored and the {@link #LAPI_READY} event will still fire.
     * </p>
     * <p>
     *     If you want to execute code right after {@link ConfigBuilder#buildLApi() building} a {@link LApi lApi} instance
     *     it is recommended to wait for this event to happen first. You can wait for it by using {@link LApi#waitUntilLApiReadyEvent()}
     *     or listening to this event.
     * </p>
     */
    LAPI_READY,

    /**
     * identifier for {@link EventListener#onVoiceRegionManagerReady(LApi, VoiceRegionManagerReadyEvent)}.
     * <br><br>
     * <p>
     *      May be called more than once if {@link VoiceRegionManager#init(int)}
     *      or {@link VoiceRegionManager#update()} is called.
     * </p>
     * <br>
     * requires:
     * <ul>
     *     <li>
     *         {@link ConfigFlag#CACHE_VOICE_REGIONS}
     *     </li>
     * </ul>
     */
    VOICE_REGION_MANAGER_READY,

    /**
     * identifier for {@link EventListener#onCacheReady(LApi, CacheReadyEvent)}.
     * <br><br>
     * <p>
     *     Triggers, when the {@link LApi#getCache() basic cache} is ready.
     * </p>
     * <br>
     * requires:
     * <ul>
     *     <li>
     *         {@link ConfigFlag#BASIC_CACHE}
     *     </li>
     * </ul>
     */
    CACHE_READY,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           RESUMED                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onResumed(LApi, ResumedEvent)}.
     * <br><br>
     */
    RESUMED,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           CHANNEL                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onChannelCreate(LApi, ChannelCreateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    CHANNEL_CREATE,

    /**
     * identifier for {@link EventListener#onChannelUpdate(LApi, ChannelUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    CHANNEL_UPDATE,

    /**
     * identifier for {@link EventListener#onChannelDelete(LApi, ChannelDeleteEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    CHANNEL_DELETE,

    /**
     * identifier for {@link EventListener#onThreadCreate(LApi, ThreadCreateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    THREAD_CREATE,

    /**
     * identifier for {@link EventListener#onThreadUpdate(LApi, ThreadUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    THREAD_UPDATE,

    /**
     * identifier for {@link EventListener#onThreadDelete(LApi, ThreadDeleteEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    THREAD_DELETE,

    /**
     * identifier for {@link EventListener#onThreadListSync(LApi, ThreadListSyncEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    THREAD_LIST_SYNC,

    /**
     * identifier for {@link EventListener#onThreadMemberUpdate(LApi, ThreadMemberUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    THREAD_MEMBER_UPDATE,

    /**
     * identifier for {@link EventListener#onThreadMembersUpdate(LApi, ThreadMembersUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    THREAD_MEMBERS_UPDATE,

    /**
     * identifier for {@link EventListener#onChannelPinsUpdate(LApi, ChannelPinsUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS} and/or {@link GatewayIntent#DIRECT_MESSAGES}
     *     </li>
     * </ul>
     */
    CHANNEL_PINS_UPDATE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             GUILD                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onGuildCreate(LApi, GuildCreateEvent)}.
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
     * identifier for {@link EventListener#onGuildDelete(LApi, GuildDeleteEvent)}.
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
     * identifier for {@link EventListener#onGuildUpdate(LApi, GuildUpdateEvent)}.
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
     * identifier for {@link EventListener#onGuildJoined(LApi, GuildJoinedEvent)}.
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
     * identifier for {@link EventListener#onGuildLeft(LApi, GuildLeftEvent)}.
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
     * identifier for {@link EventListener#onGuildUnavailable(LApi, GuildUnavailableEvent)}.
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
     * identifier for {@link EventListener#onGuildAvailable(LApi, GuildAvailableEvent)}.
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
     * identifier for {@link EventListener#onGuildBanAdd(LApi, GuildBanEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_BANS }
     *     </li>
     * </ul>
     */
    GUILD_BAN_ADD,

    /**
     * identifier for {@link EventListener#onGuildBanRemove(LApi, GuildBanEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_BANS }
     *     </li>
     * </ul>
     */
    GUILD_BAN_REMOVE,

    /**
     * identifier for {@link EventListener#onGuildEmojisUpdate(LApi, GuildEmojisUpdateEvent)}.
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
     * identifier for {@link EventListener#onGuildStickersUpdate(LApi, GuildStickersUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_EMOJIS_AND_STICKERS}
     *     </li>
     * </ul>
     */
    GUILD_STICKERS_UPDATE,

    /**
     * identifier for {@link EventListener#onGuildIntegrationsUpdate(LApi, GuildIntegrationsUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_INTEGRATIONS }
     *     </li>
     * </ul>
     */
    GUILD_INTEGRATIONS_UPDATE,

    /**
     * identifier for {@link EventListener#onGuildMemberAdd(LApi, GuildMemberAddEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_MEMBERS}
     *     </li>
     * </ul>
     */
    GUILD_MEMBER_ADD,

    /**
     * identifier for {@link EventListener#onGuildMemberUpdate(LApi, GuildMemberUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_MEMBERS}
     *     </li>
     * </ul>
     */
    GUILD_MEMBER_UPDATE,

    /**
     * identifier for {@link EventListener#onGuildMemberRemove(LApi, GuildMemberRemoveEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_MEMBERS}
     *     </li>
     * </ul>
     */
    GUILD_MEMBER_REMOVE,

    /**
     * identifier for {@link EventListener#onGuildMembersChunk(LApi, GuildMembersChunkEvent)}.
     * <br><br>
     */
    GUILD_MEMBERS_CHUNK,

    /**
     * identifier for {@link EventListener#onGuildRoleCreate(LApi, GuildRoleCreateEvent)}.
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
     * identifier for {@link EventListener#onGuildRoleUpdate(LApi, GuildRoleUpdateEvent)}.
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
     * identifier for {@link EventListener#onGuildRoleUpdate(LApi, GuildRoleUpdateEvent)}.
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
     *                     GuildScheduledEvent                       *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onGuildScheduledEventCreate(LApi, GuildScheduledEventEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_SCHEDULED_EVENTS}
     *     </li>
     * </ul>
     */
    GUILD_SCHEDULED_EVENT_CREATE,

    /**
     * identifier for {@link EventListener#onGuildScheduledEventUpdate(LApi, GuildScheduledEventEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_SCHEDULED_EVENTS}
     *     </li>
     * </ul>
     */
    GUILD_SCHEDULED_EVENT_UPDATE,

    /**
     * identifier for {@link EventListener#onGuildScheduledEventDelete(LApi, GuildScheduledEventEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_SCHEDULED_EVENTS}
     *     </li>
     * </ul>
     */
    GUILD_SCHEDULED_EVENT_DELETE,

    /**
     * identifier for {@link EventListener#onGuildScheduledEventUserAdd(LApi, GuildScheduledEventUserEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_SCHEDULED_EVENTS}
     *     </li>
     * </ul>
     */
    GUILD_SCHEDULED_EVENT_USER_ADD,

    /**
     * identifier for {@link EventListener#onGuildScheduledEventUserRemove(LApi, GuildScheduledEventUserEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_SCHEDULED_EVENTS}
     *     </li>
     * </ul>
     */
    GUILD_SCHEDULED_EVENT_USER_REMOVE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                         Integration                           *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onIntegrationCreate(LApi, IntegrationCreateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_INTEGRATIONS}
     *     </li>
     * </ul>
     */
    INTEGRATION_CREATE,

    /**
     * identifier for {@link EventListener#onIntegrationUpdate(LApi, IntegrationUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_INTEGRATIONS}
     *     </li>
     * </ul>
     */
    INTEGRATION_UPDATE,

    /**
     * identifier for {@link EventListener#onIntegrationDelete(LApi, IntegrationDeleteEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_INTEGRATIONS}
     *     </li>
     * </ul>
     */
    INTEGRATION_DELETE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           INVITE                              *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onInviteCreate(LApi, InviteCreateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_INVITES}
     *     </li>
     * </ul>
     */
    INVITE_CREATE,

    /**
     * identifier for {@link EventListener#onInviteDelete(LApi, InviteDeleteEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_INVITES}
     *     </li>
     * </ul>
     */
    INVITE_DELETE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           MESSAGE                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onMessageCreate(LApi, MessageCreateEvent)}.
     *
     * TODO: What does this one require and also all other Message Events below
     */
    MESSAGE_CREATE,

    /**
     * identifier for {@link EventListener#onGuildMessageCreate(LApi, GuildMessageCreateEvent)}.<br>
     * sub-event of {@link #MESSAGE_CREATE}.
     */
    GUILD_MESSAGE_CREATE,

    /**
     * identifier for {@link EventListener#onNonGuildMessageCreate(LApi, MessageCreateEvent)}.<br>
     * sub-event of {@link #MESSAGE_CREATE}.
     */
    NON_GUILD_MESSAGE_CREATE,

    /**
     * identifier for {@link EventListener#onMessageUpdate(LApi, MessageUpdateEvent)}.
     */
    MESSAGE_UPDATE,

    /**
     * identifier for {@link EventListener#onMessageDelete(LApi, MessageDeleteEvent)}.
     */
    MESSAGE_DELETE,

    /**
     * identifier for {@link EventListener#onMessageDeleteBulk(LApi, MessageDeleteBulkEvent)}.
     */
    MESSAGE_DELETE_BULK,

    /**
     * identifier for {@link EventListener#onMessageReactionAdd(LApi, MessageReactionEvent)}.
     */
    MESSAGE_REACTION_ADD,

    /**
     * identifier for {@link EventListener#onMessageReactionRemove(LApi, MessageReactionEvent)}.
     */
    MESSAGE_REACTION_REMOVE,

    /**
     * identifier for {@link EventListener#onMessageReactionRemoveAll(LApi, MessageReactionRemoveAllEvent)}.
     */
    MESSAGE_REACTION_REMOVE_ALL,

    /**
     * identifier for {@link EventListener#onMessageReactionRemoveEmoji(LApi, MessageReactionRemoveEmojiEvent)}.
     */
    MESSAGE_REACTION_REMOVE_EMOJI,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                          INTERACTION                          *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onInteractionCreate(LApi, InteractionCreateEvent)}.<br>
     */
    INTERACTION_CREATE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                            PRESENCE                           *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onPresenceUpdate(LApi, PresenceUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_PRESENCES}
     *     </li>
     * </ul>
     */
    PRESENCE_UPDATE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                         Stage Instance                        *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onStageInstanceCreate(LApi, StageInstanceEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    STAGE_INSTANCE_CREATE,

    /**
     * identifier for {@link EventListener#onStageInstanceDelete(LApi, StageInstanceEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    STAGE_INSTANCE_DELETE,

    /**
     * identifier for {@link EventListener#onStageInstanceUpdate(LApi, StageInstanceEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILDS}
     *     </li>
     * </ul>
     */
    STAGE_INSTANCE_UPDATE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             TYPING                            *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onTypingStart(LApi, TypingStartEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#DIRECT_MESSAGE_TYPING DIRECT_MESSAGE_TYPING}
     *         And / or {@link GatewayIntent#GUILD_MESSAGE_TYPING GUILD_MESSAGE_TYPING}
     *     </li>
     * </ul>
     */
    TYPING_START,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           USER UPDATE                         *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onUserUpdate(LApi, UserUpdateEvent)}.
     * <br><br>
     */
    USER_UPDATE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                              VOICE                            *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onVoiceStateUpdate(LApi, VoiceStateUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_VOICE_STATES}
     *     </li>
     * </ul>
     */
    VOICE_STATE_UPDATE,

    /**
     * identifier for {@link EventListener#onVoiceServerUpdate(LApi, VoiceServerUpdateEvent)}.
     * <br><br>
     */
    VOICE_SERVER_UPDATE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             Webhooks                          *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onWebhooksUpdate(LApi, WebhooksUpdateEvent)}.
     * <br><br>
     * requires:
     * <ul>
     *     <li>
     *         {@link GatewayIntent#GUILD_WEBHOOKS}
     *     </li>
     * </ul>
     */
    WEBHOOKS_UPDATE,

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             OTHER                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * identifier for {@link EventListener#onLApiError(LApi, LApiErrorEvent)}.
     */
    LAPI_ERROR,
}
