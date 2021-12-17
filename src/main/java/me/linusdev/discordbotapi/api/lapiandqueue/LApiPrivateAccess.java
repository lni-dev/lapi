package me.linusdev.discordbotapi.api.lapiandqueue;

import me.linusdev.discordbotapi.api.VoiceRegions;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.manager.guild.GuildManager;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

public class LApiPrivateAccess implements HasLApi {

    private final @NotNull LApi lApi;


    public LApiPrivateAccess(@NotNull LApi lApi){
        this.lApi = lApi;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    public EventTransmitter getEventTransmitter(){
        return lApi.eventTransmitter;
    }

    public GatewayWebSocket getGateway(){
        return lApi.gateway;
    }

    public VoiceRegions getVoiceRegions(){
        return lApi.voiceRegions;
    }

    public GuildManager getGuildManager(){
        return lApi.guildManager;
    }
}
