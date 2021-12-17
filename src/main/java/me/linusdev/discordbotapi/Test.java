package me.linusdev.discordbotapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.GuildCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.GuildDeleteEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.GuildUpdateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.discordbotapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.discordbotapi.api.communication.gateway.presence.StatusType;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.discordbotapi.api.config.ConfigBuilder;
import me.linusdev.discordbotapi.api.config.ConfigFlag;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.message.abstracts.Message;
import me.linusdev.discordbotapi.api.objects.message.embed.Author;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.discordbotapi.helper.Helper;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.DISCORD_COM;

public class Test implements EventListener{


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();

        LApi lApi = new ConfigBuilder(Helper.getConfigPath())
                .enable(ConfigFlag.ENABLE_GATEWAY)
                .disable(ConfigFlag.LOAD_VOICE_REGIONS_ON_STARTUP)
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
        EventListener.super.onUnknownEvent(lApi, type, payload);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        EventListener.super.onReady(event);
    }

    @Override
    public void onGuildsReady(@NotNull GuildsReadyEvent event) {
        System.out.println("Guilds Ready");
    }

    @Override
    public void onGuildCreate(@NotNull GuildCreateEvent event) {
        System.out.println("Guild create: " + event.getGuild().getName());
    }

    @Override
    public void onGuildDelete(@NotNull GuildDeleteEvent event) {
        EventListener.super.onGuildDelete(event);
    }

    @Override
    public void onGuildUpdate(@NotNull GuildUpdateEvent event) {
        EventListener.super.onGuildUpdate(event);
    }

    @Override
    public void onMessageCreate(@NotNull MessageCreateEvent event) {
    }

    @Override
    public void onNonGuildMessageCreate(@NotNull MessageCreateEvent event) {
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
}
