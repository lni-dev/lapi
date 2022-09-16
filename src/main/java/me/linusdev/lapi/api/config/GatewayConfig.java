/*
 * Copyright (c) 2021-2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.config;

import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.queue.processor.DispatchEventProcessorFactory;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This config is used to set up a {@link GatewayWebSocket}.<br>
 * You should use a {@link ConfigBuilder}.
 */
@ApiStatus.Internal
public class GatewayConfig {

    private final @NotNull ApiVersion apiVersion;
    private final @NotNull GatewayEncoding encoding;
    private final @NotNull GatewayCompression compression;
    private final @NotNull String os;
    private final int largeThreshold;
    private final @Nullable Integer shardId;
    private final @Nullable Integer numShards;
    private final @NotNull SelfUserPresenceUpdater startupPresence;
    private final @NotNull GatewayIntent[] intents;
    private final int intentsAsInt;
    private final @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter;
    private final @Nullable ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter;
    private final @NotNull GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler;
    private final int dispatchEventQueueSize;
    private final @NotNull DispatchEventProcessorFactory dispatchEventProcessorFactory;

    public GatewayConfig(@NotNull ApiVersion ApiVersion, @NotNull GatewayEncoding encoding, @NotNull GatewayCompression compression, @NotNull String os, int largeThreshold, @Nullable Integer shardId, @Nullable Integer numShards, @NotNull SelfUserPresenceUpdater startupPresence, @NotNull GatewayIntent[] intents, @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter, @Nullable ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter, GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler, int dispatchEventQueueSize, @NotNull DispatchEventProcessorFactory dispatchEventProcessorFactory) {
        this.apiVersion = ApiVersion;
        this.encoding = encoding;
        this.compression = compression;
        this.os = os;
        this.largeThreshold = largeThreshold;
        this.shardId = shardId;
        this.numShards = numShards;
        this.startupPresence = startupPresence;
        this.intents = intents;
        this.intentsAsInt = GatewayIntent.toInt(intents);
        this.jsonToPayloadConverter = jsonToPayloadConverter;
        this.etfToPayloadConverter = etfToPayloadConverter;
        this.unexpectedEventHandler = unexpectedEventHandler;
        this.dispatchEventQueueSize = dispatchEventQueueSize;
        this.dispatchEventProcessorFactory = dispatchEventProcessorFactory;
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

    public @NotNull SelfUserPresenceUpdater getStartupPresence() {
        return startupPresence;
    }

    public GatewayIntent[] getIntents() {
        return intents;
    }

    public int getIntentsAsInt() {
        return intentsAsInt;
    }

    public boolean hasIntent(@NotNull GatewayIntent intent) {
        return intent.isSet(getIntentsAsInt());
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

    public int getDispatchEventQueueSize() {
        return dispatchEventQueueSize;
    }

    public @NotNull DispatchEventProcessorFactory getDispatchEventProcessorFactory() {
        return dispatchEventProcessorFactory;
    }
}
