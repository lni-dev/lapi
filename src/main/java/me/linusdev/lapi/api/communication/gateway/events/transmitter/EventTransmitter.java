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
import me.linusdev.lapi.api.communication.gateway.events.Event;
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
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.manager.command.event.CommandManagerInitializedEvent;
import me.linusdev.lapi.api.manager.command.event.CommandManagerReadyEvent;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManagerReadyEvent;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.list.LinusLinkedList;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import static me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier.*;

@ApiStatus.Internal
public class EventTransmitter implements HasLApi, EventListener, AbstractEventTransmitter {

    private final @NotNull LApiImpl lApi;

    private final LinusLinkedList<AnyEventListener> anyEventListeners = new LinusLinkedList<>();
    private final LinusLinkedList<EventListener> listeners = new LinusLinkedList<>();
    private final EventIdentifierList specifiedListeners = new EventIdentifierList();

    private final AtomicBoolean triggeredGuildsReadyEvent = new AtomicBoolean(false);
    private final AtomicBoolean triggeredLApiReadyEvent = new AtomicBoolean(false);

    public EventTransmitter(@NotNull LApiImpl lApi){
        this.lApi = lApi;
    }

    @Override
    public void addListener(@NotNull EventListener listener){
        listeners.add(listener);
    }

    @Override
    public boolean removeListener(@NotNull EventListener listener){
        return listeners.remove(listener);
    }

    @Override
    public void addAnyEventListener(@NotNull AnyEventListener listener) {
        anyEventListeners.add(listener);
    }

    @Override
    public void removeAnyEventListener(@NotNull AnyEventListener listener) {
        anyEventListeners.remove(listener);
    }

    @Override
    public void addSpecifiedListener(@NotNull EventListener listener, @NotNull EventIdentifier... specifications){
        for(EventIdentifier spec : specifications){
            specifiedListeners.put(spec, listener);
        }
    }

    @Override
    public boolean removeSpecifiedListener(@NotNull EventListener listener, @NotNull EventIdentifier... specifications){
        boolean r = true;

        for(EventIdentifier spec : specifications){
            LinusLinkedList<EventListener> listeners = specifiedListeners.get(spec);
            if(listeners == null){
                r = false;
                continue;
            }
            r = r && listeners.remove(listener);
        }

        return r;
    }

    private <E extends Event> void transmitForEachListener(@NotNull E event, @NotNull EventIdentifier identifier, @NotNull EventConsumer<E> consumer) {
        if(anyEventListeners.size() > 0) {
            for(AnyEventListener listener : anyEventListeners) {
                try {
                    listener.onEvent(lApi, event, identifier);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }

        for(EventListener listener : listeners){
            try {
                consumer.accept(listener, lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        LinusLinkedList<EventListener> listeners = specifiedListeners.get(identifier);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    consumer.accept(listener, lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }




    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                            Events                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload) {
        for(EventListener listener : listeners){
            try {
                listener.onUnknownEvent(lApi, type, payload);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        LinusLinkedList<EventListener> listeners = specifiedListeners.get(UNKNOWN);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onUnknownEvent(lApi, type, payload);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {
        //Reset this here, so a new GuildsReady event can occur
        //This is required, if the gateway has reconnected
        triggeredGuildsReadyEvent.set(false);

        transmitForEachListener(event, READY, EventListener::onReady);
    }

    @Override
    public void onGuildsReady(@NotNull LApi lApi, @NotNull GuildsReadyEvent event) {
        triggeredGuildsReadyEvent.set(true);

        transmitForEachListener(event, GUILDS_READY, EventListener::onGuildsReady);
    }

    @Override
    public void onLApiReady(@NotNull LApi lApi, @NotNull LApiReadyEvent event) {
        //make sure this event is only called once
        if(triggeredLApiReadyEvent.get()) return;
        triggeredLApiReadyEvent.set(true);

        transmitForEachListener(event, LAPI_READY, EventListener::onLApiReady);
    }

    @Override
    public void onVoiceRegionManagerReady(@NotNull LApi lApi, @NotNull VoiceRegionManagerReadyEvent event) {
        transmitForEachListener(event, VOICE_REGION_MANAGER_READY, EventListener::onVoiceRegionManagerReady);
    }

    @Override
    public void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {
        transmitForEachListener(event, CACHE_READY, EventListener::onCacheReady);
    }

    @Override
    public void onCommandManagerInitialized(@NotNull LApi lApi, @NotNull CommandManagerInitializedEvent event) {
        transmitForEachListener(event, COMMAND_MANAGER_INITIALIZED, EventListener::onCommandManagerInitialized);
    }

    @Override
    public void onCommandManagerReady(@NotNull LApi lApi, @NotNull CommandManagerReadyEvent event) {
        transmitForEachListener(event, COMMAND_MANAGER_READY, EventListener::onCommandManagerReady);
    }

    @Override
    public void onResumed(@NotNull LApi lApi, @NotNull ResumedEvent event) {
        transmitForEachListener(event, RESUMED, EventListener::onResumed);
    }

    @Override
    public void onChannelCreate(@NotNull LApi lApi, @NotNull ChannelCreateEvent event) {
        transmitForEachListener(event, CHANNEL_CREATE, EventListener::onChannelCreate);
    }

    @Override
    public void onChannelUpdate(@NotNull LApi lApi, @NotNull ChannelUpdateEvent event) {
        transmitForEachListener(event, CHANNEL_UPDATE, EventListener::onChannelUpdate);
    }

    @Override
    public void onChannelDelete(@NotNull LApi lApi, @NotNull ChannelDeleteEvent event) {
        transmitForEachListener(event, CHANNEL_DELETE, EventListener::onChannelDelete);
    }

    @Override
    public void onThreadCreate(@NotNull LApi lApi, @NotNull ThreadCreateEvent event) {
        transmitForEachListener(event, THREAD_CREATE, EventListener::onThreadCreate);
    }

    @Override
    public void onThreadUpdate(@NotNull LApi lApi, @NotNull ThreadUpdateEvent event) {
        transmitForEachListener(event, THREAD_UPDATE, EventListener::onThreadUpdate);
    }

    @Override
    public void onThreadDelete(@NotNull LApi lApi, @NotNull ThreadDeleteEvent event) {
        transmitForEachListener(event, THREAD_DELETE, EventListener::onThreadDelete);
    }

    @Override
    public void onThreadListSync(@NotNull LApi lApi, @NotNull ThreadListSyncEvent event) {
        transmitForEachListener(event, THREAD_LIST_SYNC, EventListener::onThreadListSync);
    }

    @Override
    public void onThreadMemberUpdate(@NotNull LApi lApi, @NotNull ThreadMemberUpdateEvent event) {
        transmitForEachListener(event, THREAD_MEMBER_UPDATE, EventListener::onThreadMemberUpdate);
    }

    @Override
    public void onThreadMembersUpdate(@NotNull LApi lApi, @NotNull ThreadMembersUpdateEvent event) {
        transmitForEachListener(event, THREAD_MEMBERS_UPDATE, EventListener::onThreadMembersUpdate);
    }

    @Override
    public void onChannelPinsUpdate(@NotNull LApi lApi, @NotNull ChannelPinsUpdateEvent event) {
        transmitForEachListener(event, CHANNEL_PINS_UPDATE, EventListener::onChannelPinsUpdate);
    }

    @Override
    public void onGuildCreate(@NotNull LApi lApi, @NotNull GuildCreateEvent event) {
        transmitForEachListener(event, GUILD_CREATE, EventListener::onGuildCreate);

        //Sub-events
        if(!triggeredGuildsReadyEvent.get() && this.lApi.getGuildManager() != null && this.lApi.getGuildManager().allGuildsReceivedEvent()){
            //check if guild Manager is not null. if it is CACHE_GUILDS is disabled and this event can't be triggered
            onGuildsReady(lApi, new GuildsReadyEvent(lApi, this.lApi.getGuildManager()));
        }
    }

    @Override
    public void onGuildDelete(@NotNull LApi lApi, @NotNull GuildDeleteEvent event) {
        transmitForEachListener(event, GUILD_DELETE, EventListener::onGuildDelete);

        //Sub-events
        if(!triggeredGuildsReadyEvent.get() && this.lApi.getGuildManager() != null && this.lApi.getGuildManager().allGuildsReceivedEvent()){
            //check if guild Manager is not null. if it is CACHE_GUILDS is disabled and this event can't be triggered
            onGuildsReady(lApi, new GuildsReadyEvent(this.lApi, this.lApi.getGuildManager()));
        }
    }

    @Override
    public void onGuildUpdate(@NotNull LApi lApi, @NotNull GuildUpdateEvent event) {
        transmitForEachListener(event, GUILD_UPDATE, EventListener::onGuildUpdate);
    }

    @Override
    public void onGuildJoined(@NotNull LApi lApi, @NotNull GuildJoinedEvent event) {
        transmitForEachListener(event, GUILD_JOINED, EventListener::onGuildJoined);
    }

    @Override
    public void onGuildLeft(@NotNull LApi lApi, @NotNull GuildLeftEvent event) {
        transmitForEachListener(event, GUILD_LEFT, EventListener::onGuildLeft);
    }

    @Override
    public void onGuildUnavailable(@NotNull LApi lApi, @NotNull GuildUnavailableEvent event) {
        transmitForEachListener(event, GUILD_UNAVAILABLE, EventListener::onGuildUnavailable);
    }

    @Override
    public void onGuildAvailable(@NotNull LApi lApi, @NotNull GuildAvailableEvent event) {
        transmitForEachListener(event, GUILD_AVAILABLE, EventListener::onGuildAvailable);
    }

    @Override
    public void onGuildBanAdd(@NotNull LApi lApi, @NotNull GuildBanEvent event) {
        transmitForEachListener(event, GUILD_BAN_ADD, EventListener::onGuildBanAdd);
    }

    @Override
    public void onGuildBanRemove(@NotNull LApi lApi, @NotNull GuildBanEvent event) {
        transmitForEachListener(event, GUILD_BAN_REMOVE, EventListener::onGuildBanRemove);
    }

    @Override
    public void onGuildEmojisUpdate(@NotNull LApi lApi, @NotNull GuildEmojisUpdateEvent event) {
        transmitForEachListener(event, GUILD_EMOJIS_UPDATE, EventListener::onGuildEmojisUpdate);
    }

    @Override
    public void onGuildStickersUpdate(@NotNull LApi lApi, @NotNull GuildStickersUpdateEvent event) {
        transmitForEachListener(event, GUILD_STICKERS_UPDATE, EventListener::onGuildStickersUpdate);
    }

    @Override
    public void onGuildIntegrationsUpdate(@NotNull LApi lApi, @NotNull GuildIntegrationsUpdateEvent event) {
        transmitForEachListener(event, GUILD_INTEGRATIONS_UPDATE, EventListener::onGuildIntegrationsUpdate);
    }

    @Override
    public void onGuildMemberAdd(@NotNull LApi lApi, @NotNull GuildMemberAddEvent event) {
        transmitForEachListener(event, GUILD_MEMBER_ADD, EventListener::onGuildMemberAdd);
    }

    @Override
    public void onGuildMemberUpdate(@NotNull LApi lApi, @NotNull GuildMemberUpdateEvent event) {
        transmitForEachListener(event, GUILD_MEMBER_UPDATE, EventListener::onGuildMemberUpdate);
    }

    @Override
    public void onGuildMemberRemove(@NotNull LApi lApi, @NotNull GuildMemberRemoveEvent event) {
        transmitForEachListener(event, GUILD_MEMBER_REMOVE, EventListener::onGuildMemberRemove);
    }

    @Override
    public void onGuildMembersChunk(@NotNull LApi lApi, @NotNull GuildMembersChunkEvent event) {
        transmitForEachListener(event, GUILD_MEMBERS_CHUNK, EventListener::onGuildMembersChunk);
    }

    @Override
    public void onGuildRoleCreate(@NotNull LApi lApi, @NotNull GuildRoleCreateEvent event) {
        transmitForEachListener(event, GUILD_ROLE_CREATE, EventListener::onGuildRoleCreate);
    }

    @Override
    public void onGuildRoleUpdate(@NotNull LApi lApi, @NotNull GuildRoleUpdateEvent event) {
        transmitForEachListener(event, GUILD_ROLE_UPDATE, EventListener::onGuildRoleUpdate);
    }

    @Override
    public void onGuildRoleDelete(@NotNull LApi lApi, @NotNull GuildRoleDeleteEvent event) {
        transmitForEachListener(event, GUILD_ROLE_DELETE, EventListener::onGuildRoleDelete);
    }

    @Override
    public void onGuildScheduledEventCreate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        transmitForEachListener(event, GUILD_SCHEDULED_EVENT_CREATE, EventListener::onGuildScheduledEventCreate);
    }

    @Override
    public void onGuildScheduledEventUpdate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        transmitForEachListener(event, GUILD_SCHEDULED_EVENT_UPDATE, EventListener::onGuildScheduledEventUpdate);
    }

    @Override
    public void onGuildScheduledEventDelete(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        transmitForEachListener(event, GUILD_SCHEDULED_EVENT_DELETE, EventListener::onGuildScheduledEventDelete);
    }

    @Override
    public void onGuildScheduledEventUserAdd(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {
        transmitForEachListener(event, GUILD_SCHEDULED_EVENT_USER_ADD, EventListener::onGuildScheduledEventUserAdd);
    }

    @Override
    public void onGuildScheduledEventUserRemove(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {
        transmitForEachListener(event, GUILD_SCHEDULED_EVENT_USER_REMOVE, EventListener::onGuildScheduledEventUserRemove);
    }

    @Override
    public void onIntegrationCreate(@NotNull LApi lApi, @NotNull IntegrationCreateEvent event) {
        transmitForEachListener(event, INTEGRATION_CREATE, EventListener::onIntegrationCreate);
    }

    @Override
    public void onIntegrationUpdate(@NotNull LApi lApi, @NotNull IntegrationUpdateEvent event) {
        transmitForEachListener(event, INTEGRATION_UPDATE, EventListener::onIntegrationUpdate);
    }

    @Override
    public void onIntegrationDelete(@NotNull LApi lApi, @NotNull IntegrationDeleteEvent event) {
        transmitForEachListener(event, INTEGRATION_DELETE, EventListener::onIntegrationDelete);
    }

    @Override
    public void onInviteCreate(@NotNull LApi lApi, @NotNull InviteCreateEvent event) {
        transmitForEachListener(event, INVITE_CREATE, EventListener::onInviteCreate);
    }

    @Override
    public void onInviteDelete(@NotNull LApi lApi, @NotNull InviteDeleteEvent event) {
        transmitForEachListener(event, INVITE_DELETE, EventListener::onInviteDelete);
    }

    @ApiStatus.Internal
    @Override
    public void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        transmitForEachListener(event, MESSAGE_CREATE, EventListener::onMessageCreate);

        //Sub-events
        if (event.isGuildEvent()) onGuildMessageCreate(lApi, new GuildMessageCreateEvent(event));
        else onNonGuildMessageCreate(lApi, event);
    }

    @ApiStatus.Internal
    @Override
    public void onNonGuildMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        transmitForEachListener(event, NON_GUILD_MESSAGE_CREATE, EventListener::onNonGuildMessageCreate);
    }

    @ApiStatus.Internal
    @Override
    public void onGuildMessageCreate(@NotNull LApi lApi, @NotNull GuildMessageCreateEvent event) {
        transmitForEachListener(event, GUILD_MESSAGE_CREATE, EventListener::onGuildMessageCreate);
    }

    @Override
    public void onMessageUpdate(@NotNull LApi lApi, @NotNull MessageUpdateEvent event) {
        transmitForEachListener(event, MESSAGE_UPDATE, EventListener::onMessageUpdate);
    }

    @Override
    public void onMessageDelete(@NotNull LApi lApi, @NotNull MessageDeleteEvent event) {
        transmitForEachListener(event, MESSAGE_DELETE, EventListener::onMessageDelete);
    }

    @Override
    public void onMessageDeleteBulk(@NotNull LApi lApi, @NotNull MessageDeleteBulkEvent event) {
        transmitForEachListener(event, MESSAGE_DELETE_BULK, EventListener::onMessageDeleteBulk);
    }

    @Override
    public void onMessageReactionAdd(@NotNull LApi lApi, @NotNull MessageReactionEvent event) {
        transmitForEachListener(event, MESSAGE_REACTION_ADD, EventListener::onMessageReactionAdd);
    }

    @Override
    public void onMessageReactionRemove(@NotNull LApi lApi, @NotNull MessageReactionEvent event) {
        transmitForEachListener(event, MESSAGE_REACTION_REMOVE, EventListener::onMessageReactionRemove);
    }

    @Override
    public void onMessageReactionRemoveAll(@NotNull LApi lApi, @NotNull MessageReactionRemoveAllEvent event) {
        transmitForEachListener(event, MESSAGE_REACTION_REMOVE_ALL, EventListener::onMessageReactionRemoveAll);
    }

    @Override
    public void onMessageReactionRemoveEmoji(@NotNull LApi lApi, @NotNull MessageReactionRemoveEmojiEvent event) {
        transmitForEachListener(event, MESSAGE_REACTION_REMOVE_EMOJI, EventListener::onMessageReactionRemoveEmoji);
    }

    @Override
    public void onPresenceUpdate(@NotNull LApi lApi, @NotNull PresenceUpdateEvent event) {
        transmitForEachListener(event, PRESENCE_UPDATE, EventListener::onPresenceUpdate);
    }

    @Override
    public void onStageInstanceCreate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        transmitForEachListener(event, STAGE_INSTANCE_CREATE, EventListener::onStageInstanceCreate);
    }

    @Override
    public void onStageInstanceDelete(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        transmitForEachListener(event, STAGE_INSTANCE_DELETE, EventListener::onStageInstanceDelete);
    }

    @Override
    public void onStageInstanceUpdate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        transmitForEachListener(event, STAGE_INSTANCE_UPDATE, EventListener::onStageInstanceUpdate);
    }

    @Override
    public void onTypingStart(@NotNull LApi lApi, @NotNull TypingStartEvent event) {
        transmitForEachListener(event, TYPING_START, EventListener::onTypingStart);
    }

    @Override
    public void onUserUpdate(@NotNull LApi lApi, @NotNull UserUpdateEvent event) {
        transmitForEachListener(event, USER_UPDATE, EventListener::onUserUpdate);
    }

    @Override
    public void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {
        transmitForEachListener(event, INTERACTION_CREATE, EventListener::onInteractionCreate);
    }

    @Override
    public void onVoiceStateUpdate(@NotNull LApi lApi, @NotNull VoiceStateUpdateEvent event) {
        transmitForEachListener(event, VOICE_STATE_UPDATE, EventListener::onVoiceStateUpdate);
    }

    @Override
    public void onVoiceServerUpdate(@NotNull LApi lApi, @NotNull VoiceServerUpdateEvent event) {
        transmitForEachListener(event, VOICE_SERVER_UPDATE, EventListener::onVoiceServerUpdate);
    }

    @Override
    public void onWebhooksUpdate(@NotNull LApi lApi, @NotNull WebhooksUpdateEvent event) {
        transmitForEachListener(event, WEBHOOKS_UPDATE, EventListener::onWebhooksUpdate);
    }

    @Override
    public void onLApiError(@NotNull LApi lApi, @NotNull LApiErrorEvent event) {
        transmitForEachListener(event, LAPI_ERROR, EventListener::onLApiError);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
