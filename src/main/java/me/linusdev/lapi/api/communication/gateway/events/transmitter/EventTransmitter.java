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
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelDeleteEvent;
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
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.presence.PresenceUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.resumed.ResumedEvent;
import me.linusdev.lapi.api.communication.gateway.events.stage.StageInstanceEvent;
import me.linusdev.lapi.api.communication.gateway.events.thread.*;
import me.linusdev.lapi.api.communication.gateway.events.typing.TypingStartEvent;
import me.linusdev.lapi.api.communication.gateway.events.voice.state.VoiceStateUpdateEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier.*;

public class EventTransmitter implements HasLApi, EventListener, AbstractEventTransmitter {

    private final @NotNull LApiImpl lApi;

    private final ArrayList<EventListener> listeners = new ArrayList<>(1);
    private final LinkedHashMap<EventIdentifier, ArrayList<EventListener>> specifiedListeners = new LinkedHashMap<>();

    private final AtomicBoolean triggeredGuildsReadyEvent = new AtomicBoolean(false);
    private final AtomicBoolean triggeredLApiReadyEvent = new AtomicBoolean(false);

    public EventTransmitter(@NotNull LApiImpl lApi){
        this.lApi = lApi;
    }

    /**
     * @see AbstractEventTransmitter#addListener(EventListener)
     */
    @Override
    public boolean addListener(@NotNull EventListener listener){
        return listeners.add(listener);
    }

    /**
     * @see AbstractEventTransmitter#removeListener(EventListener)
     */
    @Override
    public boolean removeListener(@NotNull EventListener listener){
        return listeners.remove(listener);
    }

    /**
     * @see AbstractEventTransmitter#addSpecifiedListener(EventListener, EventIdentifier...)
     */
    @Override
    public boolean addSpecifiedListener(@NotNull EventListener listener, @NotNull EventIdentifier... specifications){

        boolean r = true;

        for(EventIdentifier spec : specifications){
            ArrayList<EventListener> listeners = specifiedListeners.computeIfAbsent(spec, k -> new ArrayList<>());
            r = r && listeners.add(listener);
        }

        return r;
    }


    /**
     * @see AbstractEventTransmitter#removeSpecifiedListener(EventListener, EventIdentifier...)
     */
    @Override
    public boolean removeSpecifiedListener(@NotNull EventListener listener, @NotNull EventIdentifier... specifications){
        boolean r = true;

        for(EventIdentifier spec : specifications){
            ArrayList<EventListener> listeners = specifiedListeners.get(spec);
            if(listeners == null){
                r = false;
                continue;
            }
            r = r && listeners.remove(listener);
        }

        return r;
    }











    @ApiStatus.Internal
    @Override
    public void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload) {
        for(EventListener listener : listeners){
            try {
                listener.onUnknownEvent(lApi, type, payload);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(UNKNOWN);
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

    @ApiStatus.Internal
    @Override
    public void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {
        //Reset this here, so a new GuildsReady event can occur
        //This is required, if the gateway has reconnected
        triggeredGuildsReadyEvent.set(false);


        for(EventListener listener : listeners){
            try{
                listener.onReady(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onReady(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildsReady(@NotNull LApi lApi, @NotNull GuildsReadyEvent event) {
        triggeredGuildsReadyEvent.set(true);
        for(EventListener listener : listeners){
            try{
                listener.onGuildsReady(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILDS_READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onGuildsReady(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onLApiReady(@NotNull LApi lApi, @NotNull LApiReadyEvent event) {
        //make sure this event is only called once
        if(triggeredLApiReadyEvent.get()) return;
        triggeredLApiReadyEvent.set(true);
        for(EventListener listener : listeners){
            try{
                listener.onLApiReady(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(LAPI_READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onLApiReady(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onResumed(@NotNull LApi lApi, @NotNull ResumedEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onResumed(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(RESUMED);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onResumed(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onChannelCreate(@NotNull LApi lApi, @NotNull ChannelCreateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onChannelCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(CHANNEL_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onChannelCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onChannelUpdate(@NotNull LApi lApi, @NotNull ChannelUpdateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onChannelUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(CHANNEL_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onChannelUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onChannelDelete(@NotNull LApi lApi, @NotNull ChannelDeleteEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onChannelDelete(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(CHANNEL_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onChannelDelete(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onThreadCreate(@NotNull LApi lApi, @NotNull ThreadCreateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onThreadCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(THREAD_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onThreadCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onThreadUpdate(@NotNull LApi lApi, @NotNull ThreadUpdateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onThreadUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(THREAD_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onThreadUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onThreadDelete(@NotNull LApi lApi, @NotNull ThreadDeleteEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onThreadDelete(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(THREAD_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onThreadDelete(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onThreadListSync(@NotNull LApi lApi, @NotNull ThreadListSyncEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onThreadListSync(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(THREAD_LIST_SYNC);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onThreadListSync(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onThreadMemberUpdate(@NotNull LApi lApi, @NotNull ThreadMemberUpdateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onThreadMemberUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(THREAD_MEMBER_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onThreadMemberUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onThreadMembersUpdate(@NotNull LApi lApi, @NotNull ThreadMembersUpdateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onThreadMembersUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(THREAD_MEMBERS_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onThreadMembersUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildCreate(@NotNull LApi lApi, @NotNull GuildCreateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onGuildCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onGuildCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }

        //Sub-events
        if(!triggeredGuildsReadyEvent.get() && this.lApi.getGuildManager() != null && this.lApi.getGuildManager().allGuildsReceivedEvent()){
            //check if guild Manager is not null. if it is CACHE_GUILDS is disabled and this event can't be triggered
            onGuildsReady(lApi, new GuildsReadyEvent(lApi, this.lApi.getGuildManager()));
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildDelete(@NotNull LApi lApi, @NotNull GuildDeleteEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onGuildDelete(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onGuildDelete(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }

        //Sub-events
        if(!triggeredGuildsReadyEvent.get() && this.lApi.getGuildManager() != null && this.lApi.getGuildManager().allGuildsReceivedEvent()){
            //check if guild Manager is not null. if it is CACHE_GUILDS is disabled and this event can't be triggered
            onGuildsReady(lApi, new GuildsReadyEvent(this.lApi, this.lApi.getGuildManager()));
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildUpdate(@NotNull LApi lApi, @NotNull GuildUpdateEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onGuildUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onGuildUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildJoined(@NotNull LApi lApi, @NotNull GuildJoinedEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onGuildJoined(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_JOINED);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onGuildJoined(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildLeft(@NotNull LApi lApi, @NotNull GuildLeftEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onGuildLeft(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_LEFT);
        if(listeners != null){
            for(EventListener listener : listeners){
                try{
                    listener.onGuildLeft(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildUnavailable(@NotNull LApi lApi, @NotNull GuildUnavailableEvent event) {
        for(EventListener listener : listeners){
            try{
                listener.onGuildUnavailable(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_UNAVAILABLE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildUnavailable(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildAvailable(@NotNull LApi lApi, @NotNull GuildAvailableEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildAvailable(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_AVAILABLE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildAvailable(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildBanAdd(@NotNull LApi lApi, @NotNull GuildBanEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildBanAdd(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_BAN_ADD);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildBanAdd(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildBanRemove(@NotNull LApi lApi, @NotNull GuildBanEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildBanRemove(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_BAN_REMOVE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildBanRemove(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildEmojisUpdate(@NotNull LApi lApi, @NotNull GuildEmojisUpdateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildEmojisUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_EMOJIS_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildEmojisUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildStickersUpdate(@NotNull LApi lApi, @NotNull GuildStickersUpdateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildStickersUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_STICKERS_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildStickersUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildIntegrationsUpdate(@NotNull LApi lApi, @NotNull GuildIntegrationsUpdateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildIntegrationsUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_INTEGRATIONS_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildIntegrationsUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildMemberAdd(@NotNull LApi lApi, @NotNull GuildMemberAddEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildMemberAdd(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_MEMBER_ADD);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildMemberAdd(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildMemberUpdate(@NotNull LApi lApi, @NotNull GuildMemberUpdateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildMemberUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_MEMBER_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildMemberUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull LApi lApi, @NotNull GuildMemberRemoveEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildMemberRemove(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_MEMBER_REMOVE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildMemberRemove(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildMembersChunk(@NotNull LApi lApi, @NotNull GuildMembersChunkEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildMembersChunk(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_MEMBERS_CHUNK);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildMembersChunk(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildRoleCreate(@NotNull LApi lApi, @NotNull GuildRoleCreateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildRoleCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_ROLE_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildRoleCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildRoleUpdate(@NotNull LApi lApi, @NotNull GuildRoleUpdateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildRoleUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_ROLE_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildRoleUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildRoleDelete(@NotNull LApi lApi, @NotNull GuildRoleDeleteEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildRoleDelete(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_ROLE_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildRoleDelete(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildScheduledEventCreate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildScheduledEventCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_SCHEDULED_EVENT_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildScheduledEventCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildScheduledEventUpdate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildScheduledEventUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_SCHEDULED_EVENT_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildScheduledEventUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildScheduledEventDelete(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildScheduledEventDelete(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_SCHEDULED_EVENT_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildScheduledEventDelete(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildScheduledEventUserAdd(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildScheduledEventUserAdd(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_SCHEDULED_EVENT_USER_ADD);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildScheduledEventUserAdd(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onGuildScheduledEventUserRemove(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildScheduledEventUserRemove(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_SCHEDULED_EVENT_USER_REMOVE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildScheduledEventUserRemove(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onMessageCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onMessageCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }

        //Sub-events
        if(event.isGuildEvent()) onGuildMessageCreate(lApi, new GuildMessageCreateEvent(event));
        else onNonGuildMessageCreate(lApi, event);
    }

    @ApiStatus.Internal
    @Override
    public void onNonGuildMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onNonGuildMessageCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(NON_GUILD_MESSAGE_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onNonGuildMessageCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildMessageCreate(@NotNull LApi lApi, @NotNull GuildMessageCreateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onGuildMessageCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_MESSAGE_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onGuildMessageCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onPresenceUpdate(@NotNull LApi lApi, @NotNull PresenceUpdateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onPresenceUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(PRESENCE_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onPresenceUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onStageInstanceCreate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onStageInstanceCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(STAGE_INSTANCE_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onStageInstanceCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onStageInstanceDelete(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onStageInstanceDelete(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(STAGE_INSTANCE_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onStageInstanceDelete(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onStageInstanceUpdate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onStageInstanceUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(STAGE_INSTANCE_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onStageInstanceUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onTypingStart(@NotNull LApi lApi, @NotNull TypingStartEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onTypingStart(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(TYPING_START);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onTypingStart(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onInteractionCreate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(INTERACTION_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onInteractionCreate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onVoiceStateUpdate(@NotNull LApi lApi, @NotNull VoiceStateUpdateEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onVoiceStateUpdate(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(VOICE_STATE_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onVoiceStateUpdate(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }

    @Override
    public void onLApiError(@NotNull LApi lApi, @NotNull LApiErrorEvent event) {
        for(EventListener listener : listeners){
            try {
                listener.onLApiError(lApi, event);
            } catch (Throwable t) {
                listener.onUncaughtException(t);
            }
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(LAPI_ERROR);
        if(listeners != null){
            for(EventListener listener : listeners){
                try {
                    listener.onLApiError(lApi, event);
                } catch (Throwable t) {
                    listener.onUncaughtException(t);
                }
            }
        }
    }


    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
