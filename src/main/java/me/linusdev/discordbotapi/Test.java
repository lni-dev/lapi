package me.linusdev.discordbotapi;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayCloseStatusCode;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayOpcode;
import me.linusdev.discordbotapi.api.communication.gateway.GatewayPayload;
import me.linusdev.discordbotapi.api.communication.gateway.GetGatewayResponse;
import me.linusdev.discordbotapi.api.communication.gateway.activity.Activity;
import me.linusdev.discordbotapi.api.communication.gateway.activity.ActivityType;
import me.linusdev.discordbotapi.api.communication.gateway.identify.ConnectionProperties;
import me.linusdev.discordbotapi.api.communication.gateway.identify.Identify;
import me.linusdev.discordbotapi.api.communication.gateway.presence.PresenceUpdate;
import me.linusdev.discordbotapi.api.communication.gateway.presence.StatusType;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.log.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();
        final LApi lApi = new LApi(Private.TOKEN, new Config(0, null));


        GatewayWebSocket gateway = new GatewayWebSocket(lApi, Private.TOKEN, ApiVersion.V9, null,
                null, "windows", null, null, null, null,
                new GatewayIntent[]{GatewayIntent.GUILDS, GatewayIntent.DIRECT_MESSAGES});

        gateway.start();

        Thread.sleep(10000);

        System.out.println("reconnecting");
        gateway.reconnect(true);
    }


}
