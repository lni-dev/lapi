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

package me.linusdev.lapi.api.manager.guild;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.lapi.api.objects.guild.GuildImpl;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.guild.UnavailableGuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard implementation of {@link GuildManager}
 */
public class LApiGuildManagerImpl implements GuildManager {

    private final @NotNull LApiImpl lApi;
    private boolean initialized = false;

    private @Nullable ConcurrentHashMap<String, CachedGuildImpl> guilds;

    public LApiGuildManagerImpl(@NotNull LApiImpl lApi){
        this.lApi = lApi;

    }

    @Override
    public void init(int initialCapacity) {
        guilds = new ConcurrentHashMap<>(initialCapacity);
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }


    @Override
    public void onReady(@NotNull ReadyEvent event){
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        for(UnavailableGuild uGuild : event.getGuilds()){
            CachedGuildImpl guild = CachedGuildImpl.fromUnavailableGuild(lApi, uGuild);
            guilds.put(guild.getId(), guild);
        }
    }

    @Override
    public GatewayWebSocket.OnGuildCreateReturn onGuildCreate(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        if(payload.getPayloadData() == null) throw new InvalidDataException((SOData) payload.getPayloadData(), "GuildImpl data is missing!");
        SOData guildData = (SOData) payload.getPayloadData();

        String guildId = (String) guildData.get(GuildImpl.ID_KEY);
        CachedGuildImpl guild = guilds.get(guildId);
        if(guild == null) {
            //Current user joined a new guild
            guild = CachedGuildImpl.fromData(lApi, guildData);
            guilds.put(guildId, guild);
            return new GatewayWebSocket.OnGuildCreateReturn(guild, true, false);

        }

        //This guild is available or became available

        if(guild.isAwaitingEvent()){
            //guild is available
            guild.updateSelfByData(guildData);
            return new GatewayWebSocket.OnGuildCreateReturn(guild, false, false);

        } else {
            //guild became available.... event!
            guild.updateSelfByData(guildData);
            return new GatewayWebSocket.OnGuildCreateReturn(guild, false, true);

        }
    }

    @Override
    public CachedGuildImpl onGuildDelete(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        if(payload.getPayloadData() == null) throw new InvalidDataException((SOData) payload.getPayloadData(), "GuildImpl data is missing!");
        UnavailableGuild uGuild = UnavailableGuild.fromData((SOData) payload.getPayloadData());

        if(uGuild.getUnavailable() == null) {
            //The unavailable field is not set, this means
            //the user was removed from the guild
            CachedGuildImpl guild = guilds.remove(uGuild.getId());
            if(guild == null) guild = CachedGuildImpl.fromUnavailableGuild(lApi, uGuild);
            guild.setRemoved(true);
            //GuildLeftEvent is invoked by GatewayWebSocket
            return guild;
        }

        //The unavailable field is set, this means, the guild is unavailable
        CachedGuildImpl guild = guilds.get(uGuild.getId());
        guild.updateSelfByData((SOData) payload.getPayloadData());
        //GuildUnavailableEvent is invoked by GatewayWebSocket
        return guild;
    }

    @Override
    public Update<CachedGuildImpl, Guild> onGuildUpdate(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        if(payload.getPayloadData() == null) throw new InvalidDataException((SOData) payload.getPayloadData(), "GuildImpl data is missing!");
        UnavailableGuild uGuild = UnavailableGuild.fromData((SOData) payload.getPayloadData());

        CachedGuildImpl guild = guilds.get(uGuild.getId());
        return new Update<CachedGuildImpl, Guild>(guild, (SOData) payload.getPayloadData());
    }

    @Override
    public @Nullable CachedGuildImpl getUpdatableGuildById(String id) {
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        if(id == null) return null;
        return guilds.get(id);
    }

    @Override
    public boolean allGuildsReceivedEvent(){
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        for(CachedGuildImpl guild : guilds.values()){
            if(guild.isAwaitingEvent()) return false;
        }

        return true;
    }

    @Override
    public @Nullable Guild getGuildById(@Nullable String guildId) {
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        if(guildId == null) return null;
        return guilds.get(guildId);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @NotNull
    @Override
    public Iterator<CachedGuildImpl> iterator() {
        if(guilds == null) throw new UnsupportedOperationException("init() not yet called!");
        return guilds.values().iterator();
    }
}
