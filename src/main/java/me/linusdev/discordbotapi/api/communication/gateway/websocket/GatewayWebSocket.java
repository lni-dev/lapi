package me.linusdev.discordbotapi.api.communication.gateway.websocket;

import me.linusdev.data.Data;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.gateway.GatewayPayload;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GatewayWebSocket implements WebSocket.Listener, HasLApi {

    public static final ExceptionConverter<String, GatewayPayload, Exception> STANDARD_JSON_TO_PAYLOAD_CONVERTER = convertible -> {
        StringReader reader = new StringReader(convertible);
        Data data = new JsonParser().readDataFromReader(reader);
        return GatewayPayload.fromData(data);
    };


    private final @NotNull LApi lApi;

    private WebSocket webSocket = null;
    private final AtomicLong lastReceivedSequence = new AtomicLong();
    private long heartbeatInterval;

    private String currentText = null;
    private ArrayList<ByteBuffer> currentBytes = null;

    private @NotNull ExceptionConverter<String, GatewayPayload, ? extends Throwable> jsonToPayloadConverter = STANDARD_JSON_TO_PAYLOAD_CONVERTER;
    private ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayload, ? extends Throwable> etfToPayloadConverter = null;

    private ErrorHandler errorHandler = null;

    private final LogInstance logger;

    public GatewayWebSocket(@NotNull LApi lApi){
        this.lApi = lApi;
        this.logger = Logger.getLogger(GatewayWebSocket.class.getSimpleName(), Logger.Type.DEBUG);
    }



    @Override
    public void onOpen(WebSocket webSocket) {

        this.webSocket = webSocket;

        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence text, boolean last) {

        if(!last){
            if(currentText == null){
                currentText = text.toString();
                return WebSocket.Listener.super.onText(webSocket, text, false);
            }
            currentText += text;
            return WebSocket.Listener.super.onText(webSocket, text, false);
        }

        if(currentText == null) currentText = text.toString();
        else currentText += text.toString();

        try {
            GatewayPayload payload = jsonToPayloadConverter.convert(currentText);
        } catch (Exception e) {
            logger.error(e);
            if(errorHandler != null) errorHandler.handle(lApi, this, e);
        }

        currentText = null;
        return WebSocket.Listener.super.onText(webSocket, text, false);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer bytes, boolean last) {
        if(!last){
            if(currentBytes == null){
                currentBytes = new ArrayList<>();
                currentBytes.add(bytes);
                return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
            }

            currentBytes.add(bytes);
            return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
        }

        if(currentBytes == null) currentBytes = new ArrayList<>(1);
        currentBytes.add(bytes);

        try {
            GatewayPayload payload = etfToPayloadConverter.convert(currentBytes);
        } catch (Exception e) {
            logger.error(e);
            if(errorHandler != null) errorHandler.handle(lApi, this, e);
        }

        currentBytes.clear();
        return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
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
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        logger.error(error);
        if(errorHandler != null) errorHandler.handle(lApi, this, error);
        WebSocket.Listener.super.onError(webSocket, error);
    }




    public void setJsonToPayloadConverter(@NotNull ExceptionConverter<String, GatewayPayload, ? extends Throwable> jsonToPayloadConverter) {
        this.jsonToPayloadConverter = jsonToPayloadConverter;
    }

    public void setEtfToPayloadConverter(ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayload, ? extends Throwable> etfToPayloadConverter) {
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
    public static interface ErrorHandler{
        public void handle(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull Throwable error);
    }
}
