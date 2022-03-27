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
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
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
            listener.onUnknownEvent(lApi, type, payload);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(UNKNOWN);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onUnknownEvent(lApi, type, payload);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //Reset this here, so a new GuildsReady event can occur
        //This is required, if the gateway has reconnected
        triggeredGuildsReadyEvent.set(false);


        for(EventListener listener : listeners){
            listener.onReady(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onReady(event);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildsReady(@NotNull GuildsReadyEvent event) {
        triggeredGuildsReadyEvent.set(true);
        for(EventListener listener : listeners){
            listener.onGuildsReady(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILDS_READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildsReady(event);
            }
        }
    }

    @Override
    public void onLApiReady(@NotNull LApiReadyEvent event) {
        //make sure this event is only called once
        if(triggeredLApiReadyEvent.get()) return;
        triggeredLApiReadyEvent.set(true);
        for(EventListener listener : listeners){
            listener.onLApiReady(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(LAPI_READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onLApiReady(event);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildCreate(@NotNull GuildCreateEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildCreate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildCreate(event);
            }
        }

        //Sub-events
        if(!triggeredGuildsReadyEvent.get() && lApi.getGuildManager() != null && lApi.getGuildManager().allGuildsReceivedEvent()){
            //check if guild Manager is not null. if it is CACHE_GUILDS is disabled and this event can't be triggered
            onGuildsReady(new GuildsReadyEvent(lApi, lApi.getGuildManager()));
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildDelete(@NotNull GuildDeleteEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildDelete(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildDelete(event);
            }
        }

        //Sub-events
        if(!triggeredGuildsReadyEvent.get() && lApi.getGuildManager() != null && lApi.getGuildManager().allGuildsReceivedEvent()){
            //check if guild Manager is not null. if it is CACHE_GUILDS is disabled and this event can't be triggered
            onGuildsReady(new GuildsReadyEvent(lApi, lApi.getGuildManager()));
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildUpdate(@NotNull GuildUpdateEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildUpdate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildUpdate(event);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildJoined(@NotNull GuildJoinedEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildJoined(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_JOINED);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildJoined(event);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildLeft(@NotNull GuildLeftEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildLeft(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_LEFT);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildLeft(event);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildUnavailable(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_UNAVAILABLE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildUnavailable(event);
            }
        }
    }

    @Override
    public void onGuildAvailable(@NotNull GuildAvailableEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildAvailable(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_AVAILABLE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildAvailable(event);
            }
        }
    }

    @Override
    public void onGuildEmojisUpdate(@NotNull GuildEmojisUpdateEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildEmojisUpdate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_EMOJIS_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildEmojisUpdate(event);
            }
        }
    }

    @Override
    public void onGuildRoleCreate(@NotNull GuildRoleCreateEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildRoleCreate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_ROLE_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildRoleCreate(event);
            }
        }
    }

    @Override
    public void onGuildRoleUpdate(@NotNull GuildRoleUpdateEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildRoleUpdate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_ROLE_UPDATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildRoleUpdate(event);
            }
        }
    }

    @Override
    public void onGuildRoleDelete(@NotNull GuildRoleDeleteEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildRoleDelete(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_ROLE_DELETE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildRoleDelete(event);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onMessageCreate(@NotNull MessageCreateEvent event) {
        for(EventListener listener : listeners){
            listener.onMessageCreate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(READY);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onMessageCreate(event);
            }
        }

        //Sub-events
        if(event.isGuildEvent()) onGuildMessageCreate(new GuildMessageCreateEvent(event));
        else onNonGuildMessageCreate(event);
    }

    @ApiStatus.Internal
    @Override
    public void onNonGuildMessageCreate(@NotNull MessageCreateEvent event) {
        for(EventListener listener : listeners){
            listener.onNonGuildMessageCreate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(NON_GUILD_MESSAGE_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onNonGuildMessageCreate(event);
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public void onGuildMessageCreate(@NotNull GuildMessageCreateEvent event) {
        for(EventListener listener : listeners){
            listener.onGuildMessageCreate(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(GUILD_MESSAGE_CREATE);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onGuildMessageCreate(event);
            }
        }
    }

    @Override
    public void onLApiError(@NotNull LApiErrorEvent event) {
        for(EventListener listener : listeners){
            listener.onLApiError(event);
        }

        ArrayList<EventListener> listeners = specifiedListeners.get(LAPI_ERROR);
        if(listeners != null){
            for(EventListener listener : listeners){
                listener.onLApiError(event);
            }
        }
    }


    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}