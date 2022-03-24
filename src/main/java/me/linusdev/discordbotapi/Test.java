package me.linusdev.discordbotapi;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.*;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.discordbotapi.api.communication.gateway.presence.StatusType;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.discordbotapi.api.config.ConfigBuilder;
import me.linusdev.discordbotapi.api.config.ConfigFlag;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.manager.guild.emoji.EmojisUpdate;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.discordbotapi.api.objects.message.abstracts.Message;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.discordbotapi.helper.Helper;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutionException;

public class Test implements EventListener{


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();

        LApi lApi = new ConfigBuilder(Helper.getConfigPath())
                .enable(ConfigFlag.ENABLE_GATEWAY)
                .enable(ConfigFlag.CACHE_ROLES)
                .enable(ConfigFlag.CACHE_GUILDS)
                .enable(ConfigFlag.COPY_GUILD_ON_UPDATE_EVENT)
                .enable(ConfigFlag.COPY_ROLE_ON_UPDATE_EVENT)
                .enable(ConfigFlag.CACHE_EMOJIS)
                .enable(ConfigFlag.COPY_EMOJI_ON_UPDATE_EVENT)
                .disable(ConfigFlag.CACHE_VOICE_REGIONS)
                .adjustGatewayConfig(gatewayConfigBuilder -> {
                    gatewayConfigBuilder
                            .setApiVersion(ApiVersion.V9)
                            .setCompression(GatewayCompression.NONE)
                            .setEncoding(GatewayEncoding.JSON)
                            .setOs("Windows 10")
                            .addIntent(GatewayIntent.ALL)
                            .adjustStartupPresence(presence -> {
                                presence.setStatus(StatusType.ONLINE);
                            });
                }).buildLapi();

        lApi.getEventTransmitter().addListener(new Test());



    }


    @Override
    public void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload) {
        System.out.println("onUnknownEvent");
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("onReady");
    }

    @Override
    public void onGuildsReady(@NotNull GuildsReadyEvent event) {
        System.out.println("onGuildsReady");

        for(CachedGuildImpl guild : event.getGuildPool()){
            System.out.println(guild);
        }
    }

    @Override
    public void onLApiReady(@NotNull LApiReadyEvent event) {
        System.out.println("onLApiReady");
    }

    @Override
    public void onGuildCreate(@NotNull GuildCreateEvent event) {
        System.out.println("GuildImpl create: " + event.getCachedGuild().getName());
    }

    @Override
    public void onGuildDelete(@NotNull GuildDeleteEvent event) {
        System.out.println("onGuildDelete");
    }

    @Override
    public void onGuildUpdate(@NotNull GuildUpdateEvent event) {
        System.out.println("onGuildUpdate");
        System.out.println(((Data)event.getPayload().getPayloadData()).getJsonString());
    }

    @Override
    public void onGuildJoined(@NotNull GuildJoinedEvent event) {
        System.out.println("onGuildJoined");
    }

    @Override
    public void onGuildLeft(@NotNull GuildLeftEvent event) {
        System.out.println("onGuildLeft");
    }

    @Override
    public void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {
        System.out.println("onGuildUnavailable");
    }

    @Override
    public void onGuildAvailable(@NotNull GuildAvailableEvent event) {
        System.out.println("onGuildAvailable");
    }

    @Override
    public void onGuildEmojisUpdate(@NotNull GuildEmojisUpdateEvent event) {
        System.out.println("onGuildEmojisUpdate");

        EmojisUpdate update = event.getUpdate();

        System.out.println("old: " + (update.getOld()));
        System.out.println("updated: " +update.getUpdated());
        System.out.println("added: " +update.getAdded());
        System.out.println("removed: " +update.getRemoved());

    }

    @Override
    public void onGuildRoleCreate(@NotNull GuildRoleCreateEvent event) {
        System.out.println("onGuildRoleCreate: " + event.getRole().getName());
    }

    @Override
    public void onGuildRoleUpdate(@NotNull GuildRoleUpdateEvent event) {
        System.out.println("onGuildRoleUpdate: old: " + event.getOldRole().getName() + ", new: " + event.getRole().getName());
    }

    @Override
    public void onGuildRoleDelete(@NotNull GuildRoleDeleteEvent event) {
        System.out.println("onGuildRoleDelete: " + event.getRole().getName());
    }

    @Override
    public void onMessageCreate(@NotNull MessageCreateEvent event) {
        System.out.println("onMessageCreate");
    }

    @Override
    public void onNonGuildMessageCreate(@NotNull MessageCreateEvent event) {
        System.out.println("onNonGuildMessageCreate");
        Message msg = event.getMessage();
        String content = msg.getContent();
        String channelId = msg.getChannelId();
        User author = msg.getAuthor();

        if(!author.isBot() && content.toLowerCase().startsWith("hi")) {

            new MessageBuilder(event.getLApi(), "Hi " + author.getUsername())
                    .setReplyTo(msg, true)
                    .getQueueable(channelId)
                    .queue();
        }
    }

    @Override
    public void onGuildMessageCreate(@NotNull GuildMessageCreateEvent event) {
        System.out.println("onGuildMessageCreate");
        Message msg = event.getMessage();
        String content = msg.getContent();
        String channelId = msg.getChannelId();
        User author = msg.getAuthor();

        if(!author.isBot() && content.toLowerCase().startsWith("hi")) {

            new MessageBuilder(event.getLApi(), "Hi " + author.getUsername())
                    .setReplyTo(msg, true)
                    .getQueueable(channelId)
                    .queue();
        }
    }

    @Override
    public void onLApiError(@NotNull LApiErrorEvent event) {
        System.out.println("onLApiError in event " + event.getInEvent() + ", error code: " + event.getError().getCode());
    }
}
