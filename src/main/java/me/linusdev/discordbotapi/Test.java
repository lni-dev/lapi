package me.linusdev.discordbotapi;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.exceptions.LimitException;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayCloseStatusCode;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayOpcode;
import me.linusdev.discordbotapi.api.communication.gateway.GatewayPayload;
import me.linusdev.discordbotapi.api.communication.gateway.GetGatewayResponse;
import me.linusdev.discordbotapi.api.communication.gateway.activity.Activity;
import me.linusdev.discordbotapi.api.communication.gateway.activity.ActivityType;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.discordbotapi.api.communication.gateway.identify.ConnectionProperties;
import me.linusdev.discordbotapi.api.communication.gateway.identify.Identify;
import me.linusdev.discordbotapi.api.communication.gateway.presence.PresenceUpdate;
import me.linusdev.discordbotapi.api.communication.gateway.presence.StatusType;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.emoji.StandardEmoji;
import me.linusdev.discordbotapi.api.objects.message.MessageImplementation;
import me.linusdev.discordbotapi.api.other.Error;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class Test {


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();
        final LApi lApi = new LApi(Private.TOKEN, new Config(0, null));

        EventTransmitter transmitter = new EventTransmitter(lApi);

        transmitter.addListener(new EventListener() {
            @Override
            public void onMessageCreate(@NotNull MessageCreateEvent event) {
                if(event.getMessage().getContent().equalsIgnoreCase("hi")){

                    try {
                        new MessageBuilder(event.getLApi()).setReplyTo(event.getMessage(), true)
                                .appendContent("Hi ").appendEmoji(StandardEmoji.values()[new Random().nextInt(StandardEmoji.values().length)])
                                .getQueueable(event.getChannelId()).queue((messageImplementation, error) -> {
                                    if(error != null){
                                        error.getThrowable().printStackTrace();
                                    }
                                });
                    } catch (LimitException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        GatewayWebSocket gateway = new GatewayWebSocket(lApi, transmitter, Private.TOKEN, ApiVersion.V9, null,
                null, "windows", null, null, null, null,
                new GatewayIntent[]{GatewayIntent.GUILDS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES});

        gateway.start();

    }


}
