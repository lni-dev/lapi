package me.linusdev.discordbotapi;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImage;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.file.types.FileType;
import me.linusdev.discordbotapi.api.communication.gateway.GatewayCloseStatusCode;
import me.linusdev.discordbotapi.api.communication.gateway.GatewayOpcode;
import me.linusdev.discordbotapi.api.communication.gateway.GatewayPayload;
import me.linusdev.discordbotapi.api.communication.gateway.GetGatewayResponse;
import me.linusdev.discordbotapi.api.communication.gateway.activity.Activity;
import me.linusdev.discordbotapi.api.communication.gateway.activity.ActivityType;
import me.linusdev.discordbotapi.api.communication.gateway.identify.ConnectionProperties;
import me.linusdev.discordbotapi.api.communication.gateway.identify.Identify;
import me.linusdev.discordbotapi.api.communication.gateway.presence.PresenceUpdate;
import me.linusdev.discordbotapi.api.communication.gateway.presence.StatusType;
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
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static me.linusdev.discordbotapi.api.lapiandqueue.LApi.CREATOR_ID;

public class Test {


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();
        final LApi lApi = new LApi(Private.TOKEN, new Config(0, null));


        GetGatewayResponse gatewayResponse = lApi.getGatewayBot().queueAndWait();

        System.out.println(gatewayResponse.getUrl());
        System.out.println(gatewayResponse.getShards());
        System.out.println(gatewayResponse.getSessionStartLimit().getData().getJsonString());

        HttpClient client = lApi.getClient();

        client.newWebSocketBuilder().header(lApi.getAuthorizationHeader().getName(), lApi.getAuthorizationHeader().getValue()).buildAsync(URI.create(gatewayResponse.getUrl() + "?v=9&encoding=json"), new WebSocket.Listener() {
            boolean started = false;
            volatile int sequence = 0;

            @Override
            public void onOpen(WebSocket webSocket) {
                WebSocket.Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(final WebSocket webSocket, CharSequence text, boolean last) {
                System.out.println("received message.... " + text);
                try {
                    Data data = new JsonParser().readDataFromReader(new StringReader(text.toString()));
                    System.out.println("message: " + data.getJsonString());

                    GatewayPayload payload = GatewayPayload.fromData(data);

                    sequence = payload.getSequence() == null ? 0 : payload.getSequence();

                    if((payload.getOpcode() == GatewayOpcode.HELLO && !started)){
                        started = true;

                        Data h = (Data) payload.getPayloadData();
                        long heartBeat = ((Number) h.get("heartbeat_interval")).longValue();

                        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                            System.out.println("sending heartbeat");
                            try{
                                GatewayPayload send = GatewayPayload.newHeartbeat(sequence);
                                webSocket.sendText(send.getData().getJsonString(), true);
                            }catch (Throwable t){
                                t.printStackTrace();
                            }

                        }, 0, heartBeat, TimeUnit.MILLISECONDS);


                        GatewayPayload identify = new GatewayPayload(
                                GatewayOpcode.IDENTIFY,
                                new Identify(
                                        Private.TOKEN,
                                        new ConnectionProperties("windows", "LApi", "LApi"),
                                        false,
                                        250,
                                        null, null,
                                        new PresenceUpdate(
                                                null,
                                                new Activity[]{new Activity("Test", ActivityType.GAME, null,
                                                        null, null, null, null,
                                                        null, null, null, null, null,
                                                        null, null, null)},
                                                StatusType.ONLINE, false),
                                        0x200),
                                -1,
                                null);

                        String s = identify.getData().getJsonString().toString();
                        System.out.println(s);
                        webSocket.sendText(s, true);


                    }else if(payload.getOpcode() == GatewayOpcode.HEARTBEAT){
                        //send heartbeat
                    }else if(payload.getOpcode() == GatewayOpcode.HEARTBEAT_ACK){
                        System.out.println("Heartbeat ack");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return WebSocket.Listener.super.onText(webSocket, text, last);
            }

            @Override
            public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                return WebSocket.Listener.super.onBinary(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
                return WebSocket.Listener.super.onPing(webSocket, message);
            }

            @Override
            public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
                return WebSocket.Listener.super.onPong(webSocket, message);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                System.out.println("close: " + reason);

                GatewayCloseStatusCode code = GatewayCloseStatusCode.fromInt(statusCode);
                System.out.println(code);

                return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                error.printStackTrace();
                WebSocket.Listener.super.onError(webSocket, error);
            }
        });
    }


}
