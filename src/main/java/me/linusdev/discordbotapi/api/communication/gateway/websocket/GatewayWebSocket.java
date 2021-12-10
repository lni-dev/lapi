package me.linusdev.discordbotapi.api.communication.gateway.websocket;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * TODO what to do if internet goes inaccessible
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#gateways" target="_top">Gateways</a>
 */
public class GatewayWebSocket implements WebSocket.Listener, HasLApi, Datable {

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

    //Generate Data keys
    public static final String SESSION_ID_KEY = "sessionId";
    public static final String CAN_RESUME_KEY = "can_resume";
    public static final String LAST_RECEIVED_SEQUENCE_KEY = "sequence";
    public static final String HEARTBEATS_SENT_KEY = "heartbeats_sent";
    public static final String HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY = "heartbeat_acks_received";
    public static final String DATA_GENERATED_TIME_MILLIS_KEY = "generated_at";


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

    private final AtomicLong heartbeatsSent;
    private final AtomicLong heartbeatAcknowledgementsReceived;

    private final AtomicBoolean canResume;

    private String currentText = null;
    private ArrayList<ByteBuffer> currentBytes = null;

    private final ScheduledExecutorService heartbeatExecutor;
    private ScheduledFuture<?> heartbeatFuture;

    //Converter and handler
    private @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter = STANDARD_JSON_TO_PAYLOAD_CONVERTER;
    private ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter = null;

    private UnexpectedEventHandler unexpectedEventHandler = null;

    /**
     * if this is above 1, we have tried to connect 2 or more times in a row. Something is probably wrong then
     */
    private final AtomicInteger pendingConnects;

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
        this.pendingConnects = new AtomicInteger(0);
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
                        if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error.getThrowable());
                        return;
                    }

                    URI uri = new URI(getGatewayResponse.getUrl()
                            + "?" + QUERY_STRING_API_VERSION_KEY + "=" + apiVersion.getVersionNumber()
                            + "&" + QUERY_STRING_ENCODING_KEY + "=" + encoding.getValue()
                            + (compression != GatewayCompression.NONE ? "&" + QUERY_STRING_COMPRESS_KEY + "=" + compression.getValue() : ""));

                    logger.debug("Gateway connecting to " + uri.toString());

                    pendingConnects.incrementAndGet();
                    builder.buildAsync(uri, this).whenComplete((webSocket, throwable) -> {
                        this.webSocket = webSocket;
                        logger.debug("build async finished");
                    });

                } catch (Exception e) {
                    logger.error(e);
                    if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, e);
                }
            });


        } catch (Exception error) {
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }

    }



    protected void handleReceivedPayload(GatewayPayloadAbstract payload) throws Throwable {
        Long seq = payload.getSequence();
        if (seq != null) this.lastReceivedSequence.set(seq);
        GatewayOpcode opcode = payload.getOpcode();

        logger.debugAlign("received payload: " + payload.toJsonString());

        if (opcode == GatewayOpcode.DISPATCH) {
            if (payload.getType() == GatewayEvent.READY) {
                logger.debug("Received " + payload.getType() + " event");
                //ready event. we need to save the session id
                ReadyEvent event = ReadyEvent.fromData(lApi, (Data) payload.getPayloadData());

                this.sessionId = event.getSessionId();
                this.canResume.set(true);
                this.pendingConnects.set(0);

            } else if (payload.getType() == GatewayEvent.RESUMED) {
                //resume successful...
                logger.debug("successfully resumed");
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
            Boolean canResume = (Boolean) payload.getPayloadData();

            unexpectedEventHandler.onInvalidSession(lApi, this, canResume != null && canResume);

            if(canResume == null || !canResume){
                resume();
            }else {
                reconnect(true);
            }

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
            logger.debug("Heartbeat ack received");

        } else {
            //This should never be sent to us... something might have gone wrong
            logger.warning("Received an " + opcode + " payload. Did something go wrong?");
        }
    }

    /**
     * will close the current WebSocket and start a new Session. Meaning:
     * This will reset {@link #sessionId}, {@link #heartbeatsSent}, {@link #heartbeatAcknowledgementsReceived},
     * {@link #heartbeatInterval}, {@link #lastReceivedSequence}
     *
     * @param sendClose whether to send a close, if the web socket is still open
     */
    public void reconnect(boolean sendClose){
        logger.debug("reconnecting...");
        heartbeatFuture.cancel(true);

        if(!webSocket.isOutputClosed() && sendClose){
            disconnect(null).whenComplete((webSocket, throwable) -> {
                webSocket.abort();
            });
            //disconnect will call resetState(), no need to call it here
            start();
            return;
        }else{
            webSocket.abort();
        }

        resetState();
        start();
    }

    /**
     * Will create a new WebSocket and resume the Session
     */
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
     * The Gateway will stop sending heartbeats or receiving any events. But a close will NOT be sent.<br>
     * This is useful, if you want to call {@link #resume(Data)} later
     */
    public void abort(){
        heartbeatFuture.cancel(true);
        webSocket.abort();
    }

    /**
     *
     * This can be used to resume another {@link GatewayWebSocket}.<br>
     * This is useful, if you want to quickly restart your os, but keeping the same Gateway connection afterwards.<br>
     * Note that this has to be done quickly, because discord will close the connection after not receiving any heartbeats
     * after a while
     *
     * @param data {@link #getData()}
     * @throws InvalidDataException if the given data was invalid
     */
    @SuppressWarnings("ConstantConditions")
    public void resume(Data data) throws InvalidDataException {
        if(webSocket != null) throw new UnsupportedOperationException("resume(Data) is exclusive to start()");

        String sessionId = (String) data.get(SESSION_ID_KEY);
        Boolean canResume = (Boolean) data.get(CAN_RESUME_KEY);
        Number lastReceivedSeq = (Number) data.get(LAST_RECEIVED_SEQUENCE_KEY);
        Number heartbeatsSent = (Number) data.get(HEARTBEATS_SENT_KEY);
        Number heartbeatsAcksReceived = (Number) data.get(HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY);
        Number genMillis = (Number) data.get(DATA_GENERATED_TIME_MILLIS_KEY);

        if(sessionId == null || canResume == null ||lastReceivedSeq == null || heartbeatsSent == null ||
        heartbeatsAcksReceived == null || genMillis == null){
            InvalidDataException.throwException(data, null, GatewayWebSocket.class,
                    new Object[]{sessionId, canResume, lastReceivedSeq, heartbeatsSent, heartbeatsAcksReceived, genMillis},
                    new String[]{SESSION_ID_KEY, CAN_RESUME_KEY, LAST_RECEIVED_SEQUENCE_KEY, HEARTBEATS_SENT_KEY, HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY, DATA_GENERATED_TIME_MILLIS_KEY});
        }

        long timePasted = System.currentTimeMillis() - genMillis.longValue();

        logger.debug("going to resume with a " + timePasted / 1000 + " seconds old GatewayResumeData");

        this.sessionId = sessionId;
        this.canResume.set(canResume);
        this.lastReceivedSequence.set(lastReceivedSeq.longValue());
        this.heartbeatsSent.set(heartbeatsSent.longValue());
        this.heartbeatAcknowledgementsReceived.set(heartbeatsAcksReceived.longValue());

        start();
    }

    /**
     * Sends {@link GatewayCloseStatusCode#SEND_CLOSE close status code} to discord.
     * Your session will be invalidated and your bot will appear offline
     *
     * @param reason string, can be {@code null}
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<WebSocket> disconnect(@Nullable String reason) {
        if (webSocket == null || webSocket.isOutputClosed())
            throw new UnsupportedOperationException("cannot disconnect, because you are not connected in the first place");

        logger.debug("sending close with reason: " + reason);
        CompletableFuture<WebSocket> future = webSocket.sendClose(GatewayCloseStatusCode.SEND_CLOSE.getCode(), reason == null ? "" : reason);

        final GatewayWebSocket _this = this;
        future = future.whenComplete((webSocket, error) -> {
            webSocket.abort();
            if (error != null) {
                logger.error(error);
                if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, _this, error);
            }
        });

        resetState();
        return future;
    }

    /**
     * Resets this {@link GatewayWebSocket}, so it could be {@link #start() started} again
     */
    protected void resetState(){
        heartbeatFuture.cancel(true);
        webSocket = null;
        sessionId = null;
        canResume.set(false);

        lastReceivedSequence.set(-1);

        heartbeatsSent.set(0);
        heartbeatAcknowledgementsReceived.set(0);
    }

    /**
     * sends a Heartbeat to discord
     */
    protected void sendHeartbeat() {
        logger.debug("sending heartbeat...");
        Long sequence = lastReceivedSequence.get();
        if (sequence == -1) sequence = null;

        GatewayPayload payload = GatewayPayload.newHeartbeat(sequence);

        sendPayload(payload).whenComplete((webSocket, throwable) -> {
            if(throwable == null) heartbeatsSent.incrementAndGet();
        });
    }

    /**
     * Sends given payload to Discord
     *
     * @param payload payload to send
     */
    protected CompletableFuture<WebSocket> sendPayload(GatewayPayloadAbstract payload) {
        if (webSocket == null || webSocket.isOutputClosed())
            throw new UnsupportedOperationException("cannot send a payload, because you are not connected");

        CompletableFuture<WebSocket> future = webSocket.sendText(payload.toJsonString(), true);

        final GatewayWebSocket _this = this;
        return future.whenComplete((webSocket, error) -> {
            if (error != null) {
                logger.error(error);
                if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, _this, error);
            }
        });
    }


    @Override
    public void onOpen(WebSocket webSocket) {
        logger.debug("onOpen");
        this.webSocket = webSocket;
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence text, boolean last) {
        try {

            if(webSocket != this.webSocket) return null;

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
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }

        return WebSocket.Listener.super.onText(webSocket, text, false);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer bytes, boolean last) {
        try {

            if(webSocket != this.webSocket) return null;

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
                if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, e);
            }

            currentBytes.clear();
        } catch (Throwable error) {
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }
        return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        if(webSocket != this.webSocket) return null;
        logger.warning("Received a ping... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        if(webSocket != this.webSocket) return null;
        logger.warning("Received a pong... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        try {
            if (webSocket != this.webSocket) return null;
            heartbeatFuture.cancel(true);

            GatewayCloseStatusCode closeCode = GatewayCloseStatusCode.fromInt(statusCode);

            logger.debug("Discord closed its output. Status-code: " + closeCode + ", reason: " + reason);

            if(pendingConnects.get() > 3){
                logger.warning("we have already tried to connect 3 times in a row without success. We wont try again");
                if(unexpectedEventHandler != null) unexpectedEventHandler.onFatal(lApi, this);
                return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }

            if(unexpectedEventHandler != null){
                if(unexpectedEventHandler.handleUnexpectedClose(lApi, this, closeCode, reason)){
                    reconnect(true);
                }
            }else{
                logger.warning("No errorHandler set! will do standard action.");
                if (closeCode == GatewayCloseStatusCode.SESSION_TIMED_OUT ||
                        closeCode == GatewayCloseStatusCode.RATE_LIMITED ||
                        closeCode == GatewayCloseStatusCode.UNKNOWN_ERROR ||
                        closeCode == GatewayCloseStatusCode.INVALID_SEQUENCE) {
                    reconnect(true);
                }else{
                    logger.error("Gateway closed");
                }
            }

        }catch (Throwable error){
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        if (webSocket != this.webSocket) return;
        logger.error(error);
        if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        WebSocket.Listener.super.onError(webSocket, error);
    }


    public void setJsonToPayloadConverter(@NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter) {
        this.jsonToPayloadConverter = jsonToPayloadConverter;
    }

    public void setEtfToPayloadConverter(ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter) {
        this.etfToPayloadConverter = etfToPayloadConverter;
    }

    public void setUnexpectedEventHandler(@Nullable GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler) {
        this.unexpectedEventHandler = unexpectedEventHandler;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public Data getData() {
        Data data = new Data(6);

        data.add(SESSION_ID_KEY, sessionId);
        data.add(CAN_RESUME_KEY, canResume.get());
        data.add(LAST_RECEIVED_SEQUENCE_KEY, lastReceivedSequence.get());
        data.add(HEARTBEATS_SENT_KEY, heartbeatsSent.get());
        data.add(HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY, heartbeatAcknowledgementsReceived.get());
        data.add(DATA_GENERATED_TIME_MILLIS_KEY, System.currentTimeMillis());

        return data;
    }


    /**
     * will handle errors occurring inside the web socket
     */
    public static interface UnexpectedEventHandler {

        /**
         *
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket} in which this error occurred
         * @param error the error
         */
        void handleError(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull Throwable error);

        /**
         *
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket} connection was closed
         * @param closeStatusCode {@link GatewayCloseStatusCode}
         * @param reason the reason Discord send us
         * @return whether the {@link GatewayWebSocket} should try to connect again
         */
        default boolean handleUnexpectedClose(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket,
                                          @NotNull GatewayCloseStatusCode closeStatusCode, String reason){
            return closeStatusCode == GatewayCloseStatusCode.SESSION_TIMED_OUT  ||
                   closeStatusCode == GatewayCloseStatusCode.RATE_LIMITED       ||
                   closeStatusCode == GatewayCloseStatusCode.UNKNOWN_ERROR      ||
                   closeStatusCode == GatewayCloseStatusCode.INVALID_SEQUENCE;
        }

        /**
         * Either we already tried to connect 3 times in a row, without success or something unexpected happened.<br>
         * Anyways, the {@link GatewayWebSocket} will <b>not</b> automatically reconnect
         *
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket}
         */
        void onFatal(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket);

        /**
         * The {@link GatewayWebSocket} received an {@link GatewayOpcode#INVALID_SESSION} from discord.
         * The {@link GatewayWebSocket} will automatically resume or reconnect after this
         *
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket}
         * @param canResume {@code true} means the {@link GatewayWebSocket} will try to {@link #resume() resume}.
         * {@code false} means the {@link GatewayWebSocket} will try to {@link #reconnect(boolean) reconnect}
         */
        void onInvalidSession(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, boolean canResume);

    }
}
