/*
 * Copyright  2022 Linus Andera
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

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.Converter;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.communication.gateway.identify.Identify;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class can build a {@link GatewayConfig}
 */
@SuppressWarnings("UnusedReturnValue")
public class GatewayConfigBuilder implements Datable {

    public static final String API_VERSION_KEY = "api_version";
    public static final String ENCODING_KEY = "encoding";
    public static final String COMPRESSION_KEY = "compression";
    public static final String OS_KEY = "os";
    public static final String LARGE_THRESHOLD_KEY = "large_threshold";
    public static final String SHARD_ID_KEY = "shard_id";
    public static final String NUM_SHARDS_KEY = "num_shards";
    public static final String STARTUP_PRESENCE_KEY = "startup_presence";
    public static final String INTENTS_KEY = "intents";

    private ApiVersion apiVersion = null;
    private GatewayEncoding encoding = null;
    private GatewayCompression compression = null;
    private String os = null;
    private Integer largeThreshold = null;
    private Integer shardId = null;
    private Integer numShards = null;
    private SelfUserPresenceUpdater startupPresence = null;
    private ArrayList<GatewayIntent> intents = new ArrayList<>();
    private ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter = null;
    private ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> bytesToPayloadConverter = null;
    private GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler = null;

    public GatewayConfigBuilder() {
        this.startupPresence = new SelfUserPresenceUpdater(false);
    }

    /**
     *
     * @return {@link Data} corresponding to this {@link GatewayConfigBuilder}
     * @see #fromData(Data)
     */
    @Override
    public Data getData() {
        Data data = new Data(9);

        data.addIfNotNull(API_VERSION_KEY, apiVersion);
        data.addIfNotNull(ENCODING_KEY, encoding);
        data.addIfNotNull(COMPRESSION_KEY, compression);
        data.addIfNotNull(OS_KEY, os);
        data.addIfNotNull(LARGE_THRESHOLD_KEY, largeThreshold);
        data.addIfNotNull(SHARD_ID_KEY, shardId);
        data.addIfNotNull(NUM_SHARDS_KEY, numShards);
        data.addIfNotNull(STARTUP_PRESENCE_KEY, startupPresence.getPresenceUpdate());
        data.addIfNotNull(INTENTS_KEY, intents);

        return data;
    }

    /**
     * Adjust this {@link GatewayConfigBuilder} depending on given data
     *
     * @param data {@link Data}
     * @return this
     * @see #getData()
     */
    public GatewayConfigBuilder fromData(@NotNull Data data) throws InvalidDataException {
        Number apiVersion = (Number) data.get(API_VERSION_KEY);
        String encoding = (String) data.get(ENCODING_KEY);
        String compression = (String) data.get(COMPRESSION_KEY);
        String os = (String) data.get(OS_KEY);
        Number largeThreshold = (Number) data.get(LARGE_THRESHOLD_KEY);
        Number shardId = (Number) data.get(SHARD_ID_KEY);
        Number numShards = (Number) data.get(NUM_SHARDS_KEY);
        Data presence = (Data) data.get(STARTUP_PRESENCE_KEY);
        ArrayList<GatewayIntent> intents = data.getAndConvertArrayList(INTENTS_KEY,
                (Converter<String, GatewayIntent>) GatewayIntent::fromName);

        this.apiVersion = apiVersion == null ? this.apiVersion : ApiVersion.fromInt(apiVersion.intValue());
        this.encoding = encoding == null ? this.encoding : GatewayEncoding.fromValue(encoding);
        this.compression = compression == null ? this.compression : GatewayCompression.fromValue(compression);
        this.os = os == null ? this.os : os;
        this.largeThreshold = largeThreshold == null ? this.largeThreshold : (Integer) largeThreshold.intValue();
        this.shardId = shardId == null ? this.shardId : (Integer) shardId.intValue();
        this.numShards = numShards == null ? this.numShards : (Integer) numShards.intValue();
        this.startupPresence = presence == null ? this.startupPresence : SelfUserPresenceUpdater.fromPresenceUpdateData(presence);
        this.intents = intents;

        return this;
    }

    /**
     * <em>Optional / Not Recommended</em><br>
     * Default: {@link LApi#NEWEST_API_VERSION}<br>
     * <p>
     * Sets the {@link ApiVersion discord api version} the {@link GatewayWebSocket gateway} should use.
     * I recommend to let this be default, other api versions are only experimentally supported.
     * </p>
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param apiVersion api version the gateway should use
     */
    public GatewayConfigBuilder setApiVersion(@Nullable ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@link GatewayEncoding#JSON}<br>
     * <p>
     * Sets the {@link GatewayEncoding encoding} the {@link GatewayWebSocket gateway} should use.
     * </p>
     * <p>
     * I recommend to let this be default. If you change this to {@link GatewayEncoding#ETF}, you will also
     * have to add a {@link #setBytesToPayloadConverter(ExceptionConverter)}.
     * </p>
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param encoding encoding the gateway should use
     */
    public GatewayConfigBuilder setEncoding(@Nullable GatewayEncoding encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@link GatewayCompression#NONE}<br>
     * <p>
     * Sets the {@link GatewayCompression compression} the {@link GatewayWebSocket gateway} should use.
     * </p>
     * <p>
     * I recommend to let this be default. If you change this to {@link GatewayCompression#ZLIB_STREAM} or
     * {@link GatewayCompression#PAYLOAD_COMPRESSION} you will have to set a
     * {@link #setJsonToPayloadConverter(ExceptionConverter) json-to-payload converter}
     * and a {@link #setBytesToPayloadConverter(ExceptionConverter) bytes-to-payload converter}.
     * </p>
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param compression compression the gateway should use
     */
    public GatewayConfigBuilder setCompression(@Nullable GatewayCompression compression) {
        this.compression = compression;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code System.getProperty("os.name")}
     * <p>
     * Sets the os, send within the
     * {@link me.linusdev.lapi.api.communication.gateway.identify.ConnectionProperties connection properties}
     * </p>
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param os os name
     */
    public GatewayConfigBuilder setOs(@Nullable String os) {
        this.os = os;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@value Identify#LARGE_THRESHOLD_STANDARD}
     * <p>
     * total number of members where Discord will stop sending offline members in the guild member list
     * </p>
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param largeThreshold large threshold, send within the {@link Identify identify}.
     */
    public GatewayConfigBuilder setLargeThreshold(@Range(from = Identify.LARGE_THRESHOLD_MIN, to = Identify.LARGE_THRESHOLD_MAX) @Nullable Integer largeThreshold) {
        this.largeThreshold = largeThreshold;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code null}
     * <p>
     * see <a href="https://discord.com/developers/docs/topics/gateway#sharding" target="_top">sharding</a> for more information
     * </p>
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param shardId shardId for this {@link GatewayWebSocket gateway}
     */
    public GatewayConfigBuilder setShardId(@Nullable Integer shardId) {
        this.shardId = shardId;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code null}
     * <p>
     * see <a href="https://discord.com/developers/docs/topics/gateway#sharding" target="_top">sharding</a> for more information
     * </p>
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param numShards see <a href="https://discord.com/developers/docs/topics/gateway#sharding" target="_top">sharding</a>
     */
    public GatewayConfigBuilder setNumShards(@Nullable Integer numShards) {
        this.numShards = numShards;
        return this;
    }

    /**
     * <em>Optional / Not Recommended</em><br>
     * Default: {@code null}
     * <p>
     *    sets the {@link SelfUserPresenceUpdater} for the gateway.
     * </p>
     * <p>
     *     Set to {@code null} to use default
     * </p>
     *
     * @param startupPresence startup presence
     */
    public GatewayConfigBuilder setSelfUserPresenceUpdater(@Nullable SelfUserPresenceUpdater startupPresence) {
        this.startupPresence = Objects.requireNonNullElseGet(startupPresence, () -> new SelfUserPresenceUpdater(false));
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code null}
     * <p>
     *    Adjusts the presence, when your bot connects to the gateway (goes online)
     * </p>
     * <p>
     *     You may not call {@link SelfUserPresenceUpdater#updateNow()}!
     * </p>
     *
     * @param consumer consumer to adjust the presence sent at the startup
     */
    public GatewayConfigBuilder adjustStartupPresence(@NotNull Consumer<SelfUserPresenceUpdater> consumer){
        consumer.accept(startupPresence.setUpdatePresence(true));
        return this;
    }

    /**
     * <em>Optional / Recommended</em>
     * <p>
     * Adds given intents to this config. Without intents, you will receive almost no events!
     * </p>
     * <p>
     * See {@link GatewayIntent intents} for a list of all intents with descriptions
     * </p>
     *
     * @param intents {@link GatewayIntent intents} to add
     * @see GatewayIntent
     */
    public GatewayConfigBuilder addIntent(@NotNull GatewayIntent... intents) {
        for (GatewayIntent intent : intents) {
            if (this.intents.contains(intent)) continue;
            this.intents.add(intent);
        }
        return this;
    }

    /**
     * <em>Optional</em><br>
     * <p>
     * Removes given intents from this config
     * </p>
     *
     * @param intents {@link GatewayIntent intents} to remove
     */
    public GatewayConfigBuilder removeIntent(@NotNull GatewayIntent... intents) {
        for (GatewayIntent intent : intents) {
            this.intents.remove(intent);
        }
        return this;
    }

    /**
     * <em>Optional / Not Recommended</em><br>
     * Default: {@link GatewayWebSocket#STANDARD_JSON_TO_PAYLOAD_CONVERTER}
     * <p>
     * Set to {@code null} to use default
     * </p>
     *
     * @param jsonToPayloadConverter the converter to convert from json-string to a payload
     */
    public GatewayConfigBuilder setJsonToPayloadConverter(@Nullable ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter) {
        this.jsonToPayloadConverter = jsonToPayloadConverter;
        return this;
    }

    /**
     * <em>Optional / Not Recommended</em><br>
     * Default: {@code null}
     *
     * @param bytesToPayloadConverter the converter to convert from bytes to a payload
     */
    public GatewayConfigBuilder setBytesToPayloadConverter(@Nullable ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> bytesToPayloadConverter) {
        this.bytesToPayloadConverter = bytesToPayloadConverter;
        return this;
    }

    /**
     * <em>Optional / Recommended</em><br>
     * Default: {@link GatewayWebSocket#STANDARD_UNEXPECTED_EVENT_HANDLER}
     * <p>
     * see {@link me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket.UnexpectedEventHandler UnexpectedEventHandler}
     * </p>
     *
     * @param unexpectedEventHandler the unexpected event handler for the {@link GatewayWebSocket}
     * @return
     */
    public GatewayConfigBuilder setUnexpectedEventHandler(@Nullable GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler) {
        this.unexpectedEventHandler = unexpectedEventHandler;
        return this;
    }

    /**
     * builds a {@link GatewayConfig}
     *
     * @return {@link GatewayConfig}
     */
    @ApiStatus.Internal
    @Contract(value = "-> new", pure = true)
    GatewayConfig build() {
        if (apiVersion == null) apiVersion = LApi.NEWEST_API_VERSION;
        if (encoding == null) encoding = GatewayEncoding.JSON;
        if (compression == null) compression = GatewayCompression.NONE;
        if (os == null) os = System.getProperty("os.name");
        if (largeThreshold == null) largeThreshold = Identify.LARGE_THRESHOLD_STANDARD;

        if (jsonToPayloadConverter == null)
            jsonToPayloadConverter = GatewayWebSocket.STANDARD_JSON_TO_PAYLOAD_CONVERTER;
        if (bytesToPayloadConverter == null && compression != GatewayCompression.NONE)
            throw new LApiRuntimeException("compression is set to " + compression + ", but no etfToPayloadConverter is given!");

        if (unexpectedEventHandler == null) unexpectedEventHandler = GatewayWebSocket.STANDARD_UNEXPECTED_EVENT_HANDLER;

        return new GatewayConfig(apiVersion, encoding, compression, os, largeThreshold, shardId,
                numShards, startupPresence, intents.toArray(new GatewayIntent[0]), jsonToPayloadConverter, bytesToPayloadConverter, unexpectedEventHandler);
    }


}
