package me.linusdev.discordbotapi.api.config;

import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.presence.PresenceUpdate;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class GatewayConfig {

    private final @NotNull ApiVersion apiVersion;
    private final @NotNull GatewayEncoding encoding;
    private final @NotNull GatewayCompression compression;
    private final @NotNull String os;
    private final int largeThreshold;
    private final @Nullable Integer shardId;
    private final @Nullable Integer numShards;
    private final @Nullable PresenceUpdate startupPresence;
    private final @NotNull GatewayIntent[] intents;
    private final @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter;
    private final @Nullable ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter;
    private final @NotNull GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler;

    public GatewayConfig(@NotNull ApiVersion ApiVersion, @NotNull GatewayEncoding encoding, @NotNull GatewayCompression compression, @NotNull String os, int largeThreshold, @Nullable Integer shardId, @Nullable Integer numShards, @Nullable PresenceUpdate startupPresence, @NotNull GatewayIntent[] intents, @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter, @Nullable ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter, GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler) {
        this.apiVersion = ApiVersion;
        this.encoding = encoding;
        this.compression = compression;
        this.os = os;
        this.largeThreshold = largeThreshold;
        this.shardId = shardId;
        this.numShards = numShards;
        this.startupPresence = startupPresence;
        this.intents = intents;
        this.jsonToPayloadConverter = jsonToPayloadConverter;
        this.etfToPayloadConverter = etfToPayloadConverter;
        this.unexpectedEventHandler = unexpectedEventHandler;
    }

    public @NotNull ApiVersion getApiVersion() {
        return apiVersion;
    }

    public @NotNull GatewayEncoding getEncoding() {
        return encoding;
    }

    public @NotNull GatewayCompression getCompression() {
        return compression;
    }

    public @NotNull String getOs() {
        return os;
    }

    public int getLargeThreshold() {
        return largeThreshold;
    }

    public @Nullable Integer getShardId() {
        return shardId;
    }

    public @Nullable Integer getNumShards() {
        return numShards;
    }

    public @Nullable PresenceUpdate getStartupPresence() {
        return startupPresence;
    }

    public GatewayIntent[] getIntents() {
        return intents;
    }

    public @Nullable ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> getEtfToPayloadConverter() {
        return etfToPayloadConverter;
    }

    public @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> getJsonToPayloadConverter() {
        return jsonToPayloadConverter;
    }

    public @NotNull GatewayWebSocket.UnexpectedEventHandler getUnexpectedEventHandler() {
        return unexpectedEventHandler;
    }
}
