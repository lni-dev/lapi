package me.linusdev.discordbotapi.api.config;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.Converter;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.presence.PresenceUpdate;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

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
    private PresenceUpdate startupPresence = null;
    private ArrayList<GatewayIntent> intents = new ArrayList<>();
    private ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter = null;
    private ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter = null;

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
        data.addIfNotNull(STARTUP_PRESENCE_KEY, startupPresence);
        data.addIfNotNull(INTENTS_KEY, intents);

        return data;
    }

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
        this.largeThreshold = largeThreshold == null ? this.largeThreshold : largeThreshold.intValue();
        this.shardId = shardId == null ? this.shardId : shardId.intValue();
        this.numShards = numShards == null ? this.numShards : numShards.intValue();
        this.startupPresence = presence == null ? this.startupPresence : PresenceUpdate.fromData(presence);
        this.intents = intents;
        //TODO

        return this;
    }

    public GatewayConfigBuilder(){

    }

    public GatewayConfigBuilder setApiVersion(@Nullable ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public GatewayConfigBuilder setEncoding(@Nullable GatewayEncoding encoding) {
        this.encoding = encoding;
        return this;
    }

    public GatewayConfigBuilder setCompression(@Nullable GatewayCompression compression) {
        this.compression = compression;
        return this;
    }

    public GatewayConfigBuilder setOs(@Nullable String os) {
        this.os = os;
        return this;
    }

    public GatewayConfigBuilder setLargeThreshold(@Nullable Integer largeThreshold) {
        this.largeThreshold = largeThreshold;
        return this;
    }

    public GatewayConfigBuilder setShardId(@Nullable Integer shardId) {
        this.shardId = shardId;
        return this;
    }

    public GatewayConfigBuilder setNumShards(@Nullable Integer numShards) {
        this.numShards = numShards;
        return this;
    }

    public GatewayConfigBuilder setStartupPresence(@Nullable PresenceUpdate startupPresence) {
        this.startupPresence = startupPresence;
        return this;
    }

    public GatewayConfigBuilder addIntent(@NotNull GatewayIntent... intents){
        this.intents.addAll(Arrays.asList(intents));
        return this;
    }

    public GatewayConfigBuilder setJsonToPayloadConverter(@NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter) {
        this.jsonToPayloadConverter = jsonToPayloadConverter;
        return this;
    }

    public GatewayConfigBuilder setEtfToPayloadConverter(ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> etfToPayloadConverter) {
        this.etfToPayloadConverter = etfToPayloadConverter;
        return this;
    }

    public GatewayConfig build(){
        if(apiVersion == null) apiVersion = LApi.NEWEST_API_VERSION;
        if(encoding == null) encoding = GatewayEncoding.JSON;
        if(compression == null) compression = GatewayCompression.NONE;
        if(os == null) os = System.getProperty("os.name");
        if(largeThreshold == null) largeThreshold = GatewayWebSocket.LARGE_THRESHOLD_STANDARD;

        if(jsonToPayloadConverter == null) jsonToPayloadConverter = GatewayWebSocket.STANDARD_JSON_TO_PAYLOAD_CONVERTER;
        if(etfToPayloadConverter == null && compression == GatewayCompression.ZLIB_STREAM)
            throw new LApiRuntimeException("compression is set to " + compression + ", but no etfToPayloadConverter is given!");


        return new GatewayConfig(apiVersion, encoding, compression, os, largeThreshold, shardId,
                numShards, startupPresence, intents.toArray(new GatewayIntent[0]), jsonToPayloadConverter, etfToPayloadConverter);
    }


}
