package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.Guild;
import me.linusdev.discordbotapi.api.objects.guild.GuildAbstract;
import me.linusdev.discordbotapi.api.objects.guild.UnavailableGuild;
import me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class LApiGuildManager implements GuildManager {

    private final @NotNull LApi lApi;

    private final @NotNull ConcurrentHashMap<String, UpdatableGuild> guilds;

    public LApiGuildManager(@NotNull LApi lApi){
        this.lApi = lApi;
        guilds = new ConcurrentHashMap<>();
    }


    @Override
    public void onReady(@NotNull ReadyEvent event){
        for(UnavailableGuild uGuild : event.getGuilds()){
            UpdatableGuild guild = UpdatableGuild.fromUnavailableGuild(getLApi(), uGuild);
            guilds.put(guild.getId(), guild);
        }
    }

    @Override
    public GatewayWebSocket.OnGuildCreateReturn onGuildCreate(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(payload.getPayloadData() == null) throw new InvalidDataException((Data) payload.getPayloadData(), "Guild data is missing!");
        Data guildData = (Data) payload.getPayloadData();

        String guildId = (String) guildData.get(Guild.ID_KEY);
        UpdatableGuild guild = guilds.get(guildId);
        if(guild == null) {
            //Current user joined a new guild
            guild = UpdatableGuild.fromData(lApi, guildData);
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
    public UpdatableGuild onGuildDelete(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(payload.getPayloadData() == null) throw new InvalidDataException((Data) payload.getPayloadData(), "Guild data is missing!");
        UnavailableGuild uGuild = UnavailableGuild.fromData((Data) payload.getPayloadData());

        if(uGuild.getUnavailable() == null) {
            //The unavailable field is not set, this means
            //the user was removed from the guild
            UpdatableGuild guild = guilds.remove(uGuild.getId());
            if(guild == null) guild = UpdatableGuild.fromUnavailableGuild(lApi, uGuild);
            guild.setRemoved(true);
            //GuildLeftEvent is invoked by GatewayWebSocket
            return guild;
        }

        //The unavailable field is set, this means, the guild is unavailable
        UpdatableGuild guild = guilds.get(uGuild.getId());
        guild.updateSelfByData((Data) payload.getPayloadData());
        //GuildUnavailableEvent is invoked by GatewayWebSocket
        return guild;
    }

    @Override
    public UpdatableGuild onGuildUpdate(@NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        if(payload.getPayloadData() == null) throw new InvalidDataException((Data) payload.getPayloadData(), "Guild data is missing!");
        UnavailableGuild uGuild = UnavailableGuild.fromData((Data) payload.getPayloadData());

        UpdatableGuild guild = guilds.get(uGuild.getId());
        guild.updateSelfByData((Data) payload.getPayloadData());
        return guild;
    }

    @Override
    public boolean allGuildsReceivedEvent(){
        for(UpdatableGuild guild : guilds.values()){
            if(guild.isAwaitingEvent()) return false;
        }

        return true;
    }

    @Override
    public @Nullable GuildAbstract getGuildById(@Nullable String guildId) {
        if(guildId == null) return null;
        return guilds.get(guildId);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
