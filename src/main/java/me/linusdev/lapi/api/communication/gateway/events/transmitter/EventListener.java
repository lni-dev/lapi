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
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
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
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
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
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManagerReadyEvent;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <h1 style="padding:0;margin:0;">Event Listener</h2>
 * <p>
 *     This listener can listen to events if added to the {@link AbstractEventTransmitter EventTransmitter}.
 * </p>
 * <p>
 *     Even though all methods are {@code default} they do not have any code in them, and you should not call any
 *     super methods. The methods are {@code default} simply for the reason, that you wont need all of them every time.
 *     The only listener with some default code is the {@link #onUncaughtException(Throwable)}, which will log the Exception.
 * </p>
 * <p>
 *     If you want to execute time consuming operations in a listener, you should always start a new thread.
 *     You should also never use {@link Object#wait() wait()} or any other waiting operation inside any listener.
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

    /**
     * This will be called, if an {@link Exception} or {@link Throwable} was thrown inside this
     * {@link EventListener EventListener's} methods.<br>
     * Note: This method should <b>never</b> throw an Exception!
     * @param uncaught {@link Throwable} that was not caught.
     */
    default void onUncaughtException(Throwable uncaught) {
        try{
            LogInstance log = Logger.getLogger(this.getClass());
            log.error("Uncaught exception in an event listener:");
            log.error(uncaught);
        }catch (Throwable printToConsole){
            printToConsole.printStackTrace();
        }
    }



    default void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload){}



    default void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {}

    /**
     * This event will be triggered, when data for all guilds, the current user is a member of,
     * has been retrieved.<br>
     * This can also happen randomly, if the gateway
     * {@link me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket#reconnect(boolean) reconnects}.
     */
    default void onGuildsReady(@NotNull LApi lApi, @NotNull GuildsReadyEvent event) {}

    /**
     * @see EventIdentifier#LAPI_READY
     */
    default void onLApiReady(@NotNull LApi lApi, @NotNull LApiReadyEvent event) {}

    default void onVoiceRegionManagerReady(@NotNull LApi lApi, @NotNull VoiceRegionManagerReadyEvent event) {}

    default void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {}

    default void onResumed(@NotNull LApi lApi, @NotNull ResumedEvent event) {}

    default void onChannelCreate(@NotNull LApi lApi, @NotNull ChannelCreateEvent event) {}

    default void onChannelUpdate(@NotNull LApi lApi, @NotNull ChannelUpdateEvent event) {}

    default void onChannelDelete(@NotNull LApi lApi, @NotNull ChannelDeleteEvent event) {}

    default void onThreadCreate(@NotNull LApi lApi, @NotNull ThreadCreateEvent event) {}

    default void onThreadUpdate(@NotNull LApi lApi, @NotNull ThreadUpdateEvent event) {}

    default void onThreadDelete(@NotNull LApi lApi, @NotNull ThreadDeleteEvent event) {}

    default void onThreadListSync(@NotNull LApi lApi, @NotNull ThreadListSyncEvent event) {}

    default void onThreadMemberUpdate(@NotNull LApi lApi, @NotNull ThreadMemberUpdateEvent event) {}

    default void onThreadMembersUpdate(@NotNull LApi lApi, @NotNull ThreadMembersUpdateEvent event) {}

    default void onChannelPinsUpdate(@NotNull LApi lApi, @NotNull ChannelPinsUpdateEvent event) {}

    default void onGuildCreate(@NotNull LApi lApi, @NotNull GuildCreateEvent event) {}

    default void onGuildDelete(@NotNull LApi lApi, @NotNull GuildDeleteEvent event) {}

    default void onGuildUpdate(@NotNull LApi lApi, @NotNull GuildUpdateEvent event) {}

    /**
     * Triggered, when the current user joined a guild
     */
    default void onGuildJoined(@NotNull LApi lApi, @NotNull GuildJoinedEvent event) {}

    /**
     * Triggered, when the current user left a guild
     */
    default void onGuildLeft(@NotNull LApi lApi, @NotNull GuildLeftEvent event) {}

    /**
     * If a guild becomes unavailable (or is already unavailable when the gateway initializes) this event will trigger.
     */
    default void onGuildUnavailable(@NotNull LApi lApi, @NotNull GuildUnavailableEvent event) {}

    /**
     * Triggered when an unavailable guild (due to an outage) becomes available again
     */
    default void onGuildAvailable(@NotNull LApi lApi, @NotNull GuildAvailableEvent event) {}

    default void onGuildBanAdd(@NotNull LApi lApi, @NotNull GuildBanEvent event) {}

    default void onGuildBanRemove(@NotNull LApi lApi, @NotNull GuildBanEvent event) {}

    default void onGuildEmojisUpdate(@NotNull LApi lApi, @NotNull GuildEmojisUpdateEvent event) {}

    default void onGuildStickersUpdate(@NotNull LApi lApi, @NotNull GuildStickersUpdateEvent event) {}

    default void onGuildIntegrationsUpdate(@NotNull LApi lApi, @NotNull GuildIntegrationsUpdateEvent event) {}

    default void onGuildMemberAdd(@NotNull LApi lApi, @NotNull GuildMemberAddEvent event) {}

    default void onGuildMemberUpdate(@NotNull LApi lApi, @NotNull GuildMemberUpdateEvent event) {}

    default void onGuildMemberRemove(@NotNull LApi lApi, @NotNull GuildMemberRemoveEvent event) {}

    default void onGuildMembersChunk(@NotNull LApi lApi, @NotNull GuildMembersChunkEvent event) {}

    default void onGuildRoleCreate(@NotNull LApi lApi, @NotNull GuildRoleCreateEvent event) {}

    default void onGuildRoleUpdate(@NotNull LApi lApi, @NotNull GuildRoleUpdateEvent event) {}

    default void onGuildRoleDelete(@NotNull LApi lApi, @NotNull GuildRoleDeleteEvent event) {}

    default void onGuildScheduledEventCreate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {}

    default void onGuildScheduledEventUpdate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {}

    default void onGuildScheduledEventDelete(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {}

    default void onGuildScheduledEventUserAdd(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {}

    default void onGuildScheduledEventUserRemove(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {}

    default void onIntegrationCreate(@NotNull LApi lApi, @NotNull IntegrationCreateEvent event) {}

    default void onIntegrationUpdate(@NotNull LApi lApi, @NotNull IntegrationUpdateEvent event) {}

    default void onIntegrationDelete(@NotNull LApi lApi, @NotNull IntegrationDeleteEvent event) {}

    default void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {}

    default void onInviteCreate(@NotNull LApi lApi, @NotNull InviteCreateEvent event) {}

    default void onInviteDelete(@NotNull LApi lApi, @NotNull InviteDeleteEvent event) {}

    default void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {}

    default void onNonGuildMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {}

    default void onGuildMessageCreate(@NotNull LApi lApi, @NotNull GuildMessageCreateEvent event) {}

    default void onMessageUpdate(@NotNull LApi lApi, @NotNull MessageUpdateEvent event) {}

    default void onMessageDelete(@NotNull LApi lApi, @NotNull MessageDeleteEvent event) {}

    default void onMessageDeleteBulk(@NotNull LApi lApi, @NotNull MessageDeleteBulkEvent event) {}

    default void onMessageReactionAdd(@NotNull LApi lApi, @NotNull MessageReactionEvent event) {}

    default void onMessageReactionRemove(@NotNull LApi lApi, @NotNull MessageReactionEvent event) {}

    default void onMessageReactionRemoveAll(@NotNull LApi lApi, @NotNull MessageReactionRemoveAllEvent event) {}

    default void onMessageReactionRemoveEmoji(@NotNull LApi lApi, @NotNull MessageReactionRemoveEmojiEvent event) {}

    default void onPresenceUpdate(@NotNull LApi lApi, @NotNull PresenceUpdateEvent event) {}

    default void onStageInstanceCreate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {}

    default void onStageInstanceDelete(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {}

    default void onStageInstanceUpdate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {}

    default void onTypingStart(@NotNull LApi lApi, @NotNull TypingStartEvent event) {}

    default void onUserUpdate(@NotNull LApi lApi, @NotNull UserUpdateEvent event) {}

    default void onVoiceStateUpdate(@NotNull LApi lApi, @NotNull VoiceStateUpdateEvent event) {}

    default void onVoiceServerUpdate(@NotNull LApi lApi, @NotNull VoiceServerUpdateEvent event) {}

    default void onWebhooksUpdate(@NotNull LApi lApi, @NotNull WebhooksUpdateEvent event) {}

    default void onLApiError(@NotNull LApi lApi, @NotNull LApiErrorEvent event) { }


}

