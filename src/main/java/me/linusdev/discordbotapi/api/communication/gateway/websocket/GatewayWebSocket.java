package me.linusdev.discordbotapi.api.communication.gateway.websocket;

import me.linusdev.data.Data;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.gateway.*;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayCloseStatusCode;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayOpcode;
import me.linusdev.discordbotapi.api.communication.gateway.events.ReadyEvent;
import me.linusdev.discordbotapi.api.communication.gateway.identify.ConnectionProperties;
import me.linusdev.discordbotapi.api.communication.gateway.identify.Identify;
import me.linusdev.discordbotapi.api.communication.gateway.presence.PresenceUpdate;
import me.linusdev.discordbotapi.api.communication.gateway.resume.Resume;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class GatewayWebSocket implements WebSocket.Listener, HasLApi {

    public static final int LARGE_THRESHOLD_STANDARD = 50;

    public static final String QUERY_STRING_API_VERSION_KEY = "v";
    public static final String QUERY_STRING_ENCODING_KEY = "encoding";
    public static final String QUERY_STRING_COMPRESS_KEY = "compress";

    public static final String HEARTBEAT_INTERVAL_KEY = "heartbeat_interval";

    public static final ExceptionConverter<String, GatewayPayloadAbstract, Exception> STANDARD_JSON_TO_PAYLOAD_CONVERTER = convertible -> {
        StringReader reader = new StringReader(convertible);
        Data data = new JsonParser().readDataFromReader(reader);
        return GatewayPayload.fromData(data);
    };


    private final @NotNull LApi lApi;

    private final @NotNull String token;
    private final @NotNull ConnectionProperties properties;

    private final @NotNull ApiVersion apiVersion;
    private final @NotNull GatewayEncoding encoding;
    private final @NotNull GatewayCompression compression;
    private final int largeThreshold;
    private final boolean usesSharding;
    private final int shardId;
    private final int numShards;
    private @Nullable PresenceUpdate presence;
    private final @NotNull GatewayIntent[] intents;

    private WebSocket webSocket = null;
    /**
     * The last sequence received from Discord. -1 if we haven't received one yet
     */
    private final AtomicLong lastReceivedSequence;
    private long heartbeatInterval;
    private String sessionId;

    private AtomicLong heartbeatsSent;
    private AtomicLong heartbeatAcknowledgementsReceived;

    private AtomicBoolean canResume;

    private String currentText = null;
    private ArrayList<ByteBuffer> currentBytes = null;

    private final ScheduledExecutorService heartbeatExecutor;
    private ScheduledFuture<?> heartbeatFuture;

    //Converter and handler
    private @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter = STANDARD_JSON_TO_PAYLOAD_CONVERTER;
    private ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter = null;

    private ErrorHandler errorHandler = null;

    private final LogInstance logger;

    public GatewayWebSocket(@NotNull LApi lApi, @NotNull String token, @Nullable ApiVersion apiVersion,
                            @Nullable GatewayEncoding encoding, @Nullable GatewayCompression compression,
                            @NotNull String os, @Nullable Integer largeThreshold, @Nullable Integer shardId,
                            @Nullable Integer numShards, @Nullable PresenceUpdate presence, @NotNull GatewayIntent[] intents) {
        this.lApi = lApi;
        this.token = token;
        this.properties = new ConnectionProperties(os, LApi.LAPI_NAME, LApi.LAPI_NAME);
        this.largeThreshold = largeThreshold == null ? LARGE_THRESHOLD_STANDARD : largeThreshold;
        if (shardId != null && numShards != null) {
            this.usesSharding = true;
            this.shardId = shardId;
            this.numShards = numShards;
        } else {
            this.usesSharding = false;
            this.shardId = 0;
            this.numShards = 0;
        }
        this.presence = presence;
        this.intents = intents;


        if (apiVersion == null) apiVersion = ApiVersion.V9;
        if (encoding == null) encoding = GatewayEncoding.JSON;
        if (compression == null) compression = GatewayCompression.NONE;

        this.apiVersion = apiVersion;
        this.encoding = encoding;
        this.compression = compression;

        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

        this.logger = Logger.getLogger(GatewayWebSocket.class.getSimpleName(), Logger.Type.DEBUG);

        this.canResume = new AtomicBoolean(false);

        this.lastReceivedSequence = new AtomicLong(-1);

        this.heartbeatsSent = new AtomicLong(0);
        this.heartbeatAcknowledgementsReceived = new AtomicLong(0);
    }

    public void start() {
        try {
            LApiHttpHeader authenticationHeader = lApi.getAuthorizationHeader();
            LApiHttpHeader userAgentHeader = lApi.getUserAgentHeader();

            HttpClient client = lApi.getClient();

            WebSocket.Builder builder = client.newWebSocketBuilder()
                    .header(authenticationHeader.getName(), authenticationHeader.getValue())
                    .header(userAgentHeader.getName(), userAgentHeader.getValue());

            lApi.getGatewayBot().queue((getGatewayResponse, error) -> {
                try {
                    if (error != null) {
                        logger.error(error.getThrowable());
                        if (errorHandler != null) errorHandler.handle(lApi, this, error.getThrowable());
                        return;
                    }

                    URI uri = new URI(getGatewayResponse.getUrl()
                            + "?v=" + apiVersion.getVersionNumber()
                            + "&" + QUERY_STRING_ENCODING_KEY + "=" + encoding.getValue()
                            + (compression != GatewayCompression.NONE ? "&" + QUERY_STRING_COMPRESS_KEY + "=" + compression.getValue() : ""));

                    logger.debug("Gateway connecting to " + uri.toString());

                    builder.buildAsync(uri, this).whenComplete((webSocket, throwable) -> {
                        this.webSocket = webSocket;
                    });

                } catch (Exception e) {
                    logger.error(e);
                    if (errorHandler != null) errorHandler.handle(lApi, this, e);
                }
            });


        } catch (Exception error) {
            logger.error(error);
            if (errorHandler != null) errorHandler.handle(lApi, this, error);
        }

    }

    protected void handleReceivedPayload(GatewayPayloadAbstract payload) throws Throwable {
        Long seq = payload.getSequence();
        if (seq != null) this.lastReceivedSequence.set(seq);

        GatewayOpcode opcode = payload.getOpcode();

        if (opcode == GatewayOpcode.DISPATCH) {
            if (payload.getType() == GatewayEvent.READY) {
                logger.debug("Received " + payload.getType() + " event");
                //ready event. we need to save the session id
                ReadyEvent event = ReadyEvent.fromData(lApi, (Data) payload.getPayloadData());

                this.sessionId = event.getSessionId();
                this.canResume.set(true);

            } else if (payload.getType() == GatewayEvent.RESUMED) {
                //resume successful...

            }

            //TODO handle event

        } else if (opcode == GatewayOpcode.HEARTBEAT) {
            //Discord requested us to send a Heartbeat
            sendHeartbeat();

        } else if (opcode == GatewayOpcode.RECONNECT) {
            //Discord wants us to reconnect and resume
            resume();

        } else if (opcode == GatewayOpcode.INVALID_SESSION) {
            //Session invalidated.
            //Should we reconnect and identify/resume?
            //TODO is this send if we send a disconnect?
            //TODO handle event

        } else if (opcode == GatewayOpcode.HELLO) {
            //sent after connecting
            //the payload data is a json with contains the heartbeat_interval
            Object data = payload.getPayloadData();

            Number heartbeatInterval = (Number) ((Data) data).get(HEARTBEAT_INTERVAL_KEY);
            if (heartbeatInterval == null) {
                disconnect("No " + HEARTBEAT_INTERVAL_KEY + " received");
                return;
            }

            this.heartbeatInterval = heartbeatInterval.longValue();

            this.heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(
                    this::sendHeartbeat, this.heartbeatInterval, this.heartbeatInterval, TimeUnit.MILLISECONDS);



            //check if this is an old session. if so, we should resume
            if(canResume.get()){
                Resume resume = new Resume(token, sessionId, lastReceivedSequence.get());

                GatewayPayload resumePayload = GatewayPayload.newResume(resume);
                sendPayload(resumePayload);

            }else{
                Identify identify = new Identify(token, properties, compression != GatewayCompression.NONE,
                        largeThreshold, usesSharding ? shardId : null, usesSharding ? numShards : null, presence,
                        GatewayIntent.toInt(intents));

                GatewayPayload identifyPayload = GatewayPayload.newIdentify(identify);
                sendPayload(identifyPayload);
            }

        } else if (opcode == GatewayOpcode.HEARTBEAT_ACK) {
            //our heartbeat was acknowledged
            heartbeatAcknowledgementsReceived.incrementAndGet();

        } else {
            //This should never be sent to us... something might have gone wrong
            logger.warning("Received an " + opcode + " payload. Did something go wrong?");
        }
    }

    public void resume() {
        heartbeatFuture.cancel(true);

        if(webSocket.isInputClosed() && webSocket.isOutputClosed()){
            //input and output are both closed
            start();
        }else if(webSocket.isInputClosed() && !webSocket.isOutputClosed()){
            //Discord has already closed it's output
            webSocket.abort();
            start();
        }else{
            webSocket.abort();
            start();
        }

    }

    /**
     * Sends {@link GatewayCloseStatusCode#SEND_CLOSE close status code} to discord.
     * Your session will be invalidated and your bot will appear offline
     *
     * @param reason string, can be {@code null}
     */
    public void disconnect(@Nullable String reason) {
        if (webSocket == null || webSocket.isOutputClosed())
            throw new UnsupportedOperationException("cannot disconnect, because you are not connected in the first place");
        logger.debug("sending close with reason: " + reason);
        CompletableFuture<WebSocket> future = webSocket.sendClose(GatewayCloseStatusCode.SEND_CLOSE.getCode(), reason == null ? "" : reason);

        final GatewayWebSocket _this = this;
        future.whenComplete((webSocket, error) -> {
            webSocket.abort();
            if (error != null) {
                logger.error(error);
                if (errorHandler != null) errorHandler.handle(lApi, _this, error);
            }
        });
    }

    /**
     * sends a Heartbeat to discord
     */
    protected void sendHeartbeat() {
        Long sequence = lastReceivedSequence.get();
        if (sequence == -1) sequence = null;

        GatewayPayload payload = GatewayPayload.newHeartbeat(sequence);

        sendPayload(payload);
    }

    /**
     * Sends given payload to Discord
     *
     * @param payload payload to send
     */
    protected void sendPayload(GatewayPayloadAbstract payload) {
        if (webSocket == null || webSocket.isOutputClosed())
            throw new UnsupportedOperationException("cannot send a payload, because you are not connected");

        CompletableFuture<WebSocket> future = webSocket.sendText(payload.toJsonString(), true);

        final GatewayWebSocket _this = this;
        future.whenComplete((webSocket, error) -> {
            if (error != null) {
                logger.error(error);
                if (errorHandler != null) errorHandler.handle(lApi, _this, error);
            }
        });
    }


    @Override
    public void onOpen(WebSocket webSocket) {

        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence text, boolean last) {
        try {

            if (!last) {
                if (currentText == null) {
                    currentText = text.toString();
                    return WebSocket.Listener.super.onText(webSocket, text, false);
                }
                currentText += text;
                return WebSocket.Listener.super.onText(webSocket, text, false);
            }

            if (currentText == null) currentText = text.toString();
            else currentText += text.toString();

            GatewayPayloadAbstract payload = jsonToPayloadConverter.convert(currentText);
            handleReceivedPayload(payload);

            currentText = null;
        } catch (Throwable error) {
            logger.error(error);
            if (errorHandler != null) errorHandler.handle(lApi, this, error);
        }

        return WebSocket.Listener.super.onText(webSocket, text, false);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer bytes, boolean last) {
        try {

            if (!last) {
                if (currentBytes == null) {
                    currentBytes = new ArrayList<>();
                    currentBytes.add(bytes);
                    return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
                }

                currentBytes.add(bytes);
                return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
            }

            if (currentBytes == null) currentBytes = new ArrayList<>(1);
            currentBytes.add(bytes);

            try {
                GatewayPayloadAbstract payload = etfToPayloadConverter.convert(currentBytes);
                handleReceivedPayload(payload);
            } catch (Exception e) {
                logger.error(e);
                if (errorHandler != null) errorHandler.handle(lApi, this, e);
            }

            currentBytes.clear();
        } catch (Throwable error) {
            logger.error(error);
            if (errorHandler != null) errorHandler.handle(lApi, this, error);
        }
        return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        logger.warning("Received a ping... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        logger.warning("Received a pong... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        heartbeatFuture.cancel(true);
        //TODO
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        logger.error(error);
        if (errorHandler != null) errorHandler.handle(lApi, this, error);
        WebSocket.Listener.super.onError(webSocket, error);
    }


    public void setJsonToPayloadConverter(@NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter) {
        this.jsonToPayloadConverter = jsonToPayloadConverter;
    }

    public void setEtfToPayloadConverter(ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter) {
        this.etfToPayloadConverter = etfToPayloadConverter;
    }

    public void setErrorHandler(@Nullable ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }


    /**
     * will handle errors occurring inside the web socket
     */
    public static interface ErrorHandler {
        public void handle(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull Throwable error);
    }
}
