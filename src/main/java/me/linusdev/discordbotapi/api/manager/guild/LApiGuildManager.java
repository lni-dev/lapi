package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.update.Update;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.discordbotapi.api.objects.guild.GuildImpl;
import me.linusdev.discordbotapi.api.objects.guild.Guild;
import me.linusdev.discordbotapi.api.objects.guild.UnavailableGuild;
import me.linusdev.discordbotapi.api.objects.role.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard implementation of {@link GuildManager}
 */
public class LApiGuildManager implements GuildManager {

    private final @NotNull LApiImpl lApi;

    private final @NotNull ConcurrentHashMap<String, CachedGuildImpl> guilds;

    public LApiGuildManager(@NotNull LApiImpl lApi){
        this.lApi = lApi;
        guilds = new ConcurrentHashMap<>();
    }


    @Override
    public void onReady(@NotNull ReadyEvent event){
        for(UnavailableGuild uGuild : event.getGuilds()){
            CachedGuildImpl guild = CachedGuildImpl.fromUnavailableGuild(lApi, uGuild);
            guilds.put(guild.getId(), guild);
        }
    }

    @Override
    public GatewayWebSocket.OnGuildCreateReturn onGuildCreate(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(payload.getPayloadData() == null) throw new InvalidDataException((Data) payload.getPayloadData(), "GuildImpl data is missing!");
        Data guildData = (Data) payload.getPayloadData();

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
        if(payload.getPayloadData() == null) throw new InvalidDataException((Data) payload.getPayloadData(), "GuildImpl data is missing!");
        UnavailableGuild uGuild = UnavailableGuild.fromData((Data) payload.getPayloadData());

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
        guild.updateSelfByData((Data) payload.getPayloadData());
        //GuildUnavailableEvent is invoked by GatewayWebSocket
        return guild;
    }

    @Override
    public Update<CachedGuildImpl, Guild> onGuildUpdate(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(payload.getPayloadData() == null) throw new InvalidDataException((Data) payload.getPayloadData(), "GuildImpl data is missing!");
        UnavailableGuild uGuild = UnavailableGuild.fromData((Data) payload.getPayloadData());

        CachedGuildImpl guild = guilds.get(uGuild.getId());
        return new Update<CachedGuildImpl, Guild>(guild, (Data) payload.getPayloadData());
    }

    @Override
    public @Nullable CachedGuildImpl getUpdatableGuildById(String id) {
        if(id == null) return null;
        return guilds.get(id);
    }

    @Override
    public boolean allGuildsReceivedEvent(){
        for(CachedGuildImpl guild : guilds.values()){
            if(guild.isAwaitingEvent()) return false;
        }

        return true;
    }

    @Override
    public @Nullable Guild getGuildById(@Nullable String guildId) {
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
        return guilds.values().iterator();
    }
}
