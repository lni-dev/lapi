package me.linusdev.discordbotapi.api.communication.gateway.identify;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.Converter;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.presence.PresenceUpdate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#identify-identify-structure" target="_top">Identify</a>
 */
public class Identify implements Datable {

    public static final int LARGE_THRESHOLD_MIN = 50;
    public static final int LARGE_THRESHOLD_MAX = 250;
    public static final int LARGE_THRESHOLD_STANDARD = 50;

    public static final String TOKEN_KEY = "token";
    public static final String PROPERTIES_KEY = "properties";
    public static final String COMPRESS_KEY = "compress";
    public static final String LARGE_THRESHOLD_KEY = "large_threshold";
    public static final String SHARD_KEY = "shard";
    public static final String PRESENCE_KEY = "presence";
    public static final String INTENTS_KEY = "intents";

    private final @NotNull String token;
    private final @NotNull ConnectionProperties properties;
    private final @Nullable Boolean compress;
    private final @Nullable Integer largeThreshold;
    private final @Nullable Integer shardId;
    private final @Nullable Integer shardNumber;
    private final @Nullable PresenceUpdate presence;
    private final int intents;

    /**
     *
     * @param token authentication token
     * @param properties connection properties
     * @param compress whether this connection supports compression of packets
     * @param largeThreshold value between 50 and 250, total number of members where the gateway will stop sending offline members in the guild member list
     * @param shardId used for GuildImpl Sharding
     * @param shardNumber 	used for GuildImpl Sharding
     * @param presence presence structure for initial presence information
     * @param intents the Gateway Intents you wish to receive
     */
    public Identify(@NotNull String token, @NotNull ConnectionProperties properties, @Nullable Boolean compress, @Nullable Integer largeThreshold, @Nullable Integer shardId, @Nullable Integer shardNumber, @Nullable PresenceUpdate presence, int intents) {
        this.token = token;
        this.properties = properties;
        this.compress = compress;
        this.largeThreshold = largeThreshold;
        this.shardId = shardId;
        this.shardNumber = shardNumber;
        this.presence = presence;
        this.intents = intents;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link Identify}
     * @throws InvalidDataException if {@link #TOKEN_KEY}, {@link #PROPERTIES_KEY}, {@link #INTENTS_KEY} is missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable Identify fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String token = (String) data.get(TOKEN_KEY);

        ConnectionProperties properties = data.getAndConvert(PROPERTIES_KEY,
                (ExceptionConverter<Data, ConnectionProperties, InvalidDataException>) ConnectionProperties::fromData);

        Boolean compress = (Boolean) data.get(COMPRESS_KEY);
        Number largeThreshold = (Number) data.get(LARGE_THRESHOLD_KEY);

        ArrayList<Integer> shard = data.getAndConvertArrayList(SHARD_KEY,
                (Converter<Object, Integer>) convertible -> (Integer) convertible);

        PresenceUpdate presence = data.getAndConvert(PRESENCE_KEY,
                (ExceptionConverter<Data, PresenceUpdate, InvalidDataException>) PresenceUpdate::fromData);

        Number intents = (Number) data.get(INTENTS_KEY);

        if(token == null ||properties == null || intents == null){
            InvalidDataException.throwException(data, null, InvalidDataException.class,
                    new Object[]{token, properties, intents},
                    new String[]{TOKEN_KEY, PROPERTIES_KEY, INTENTS_KEY});
        }

        //noinspection ConstantConditions
        return new Identify(token, properties, compress,
                largeThreshold == null ? null : largeThreshold.intValue(),
                shard == null ? null : shard.get(0),
                shard == null ? null : shard.get(1), presence, intents.intValue());
    }

    /**
     * authentication token
     */
    public @NotNull String getToken() {
        return token;
    }

    /**
     * 	connection properties
     */
    public @NotNull ConnectionProperties getProperties() {
        return properties;
    }

    /**
     * whether this connection supports compression of packets
     */
    public @Nullable Boolean getCompress() {
        return compress;
    }

    /**
     * value between 50 and 250, total number of members where the gateway will stop sending offline members in the guild member list
     */
    public @Nullable Integer getLargeThreshold() {
        return largeThreshold;
    }

    /**
     * used for GuildImpl Sharding
     */
    public @Nullable Integer getShardId() {
        return shardId;
    }

    /**
     * used for GuildImpl Sharding
     */
    public @Nullable Integer getShardNumber() {
        return shardNumber;
    }

    /**
     * presence structure for initial presence information
     */
    public @Nullable PresenceUpdate getPresence() {
        return presence;
    }

    /**
     * the Gateway Intents you wish to receive
     */
    public int getIntents() {
        return intents;
    }

    @Override
    public Data getData() {
        Data data = new Data(7);

        data.add(TOKEN_KEY, token);
        data.add(PROPERTIES_KEY, properties);
        data.addIfNotNull(COMPRESS_KEY, compress);
        data.addIfNotNull(LARGE_THRESHOLD_KEY, largeThreshold);
        if(shardId != null && shardNumber != null) data.add(SHARD_KEY, new int[]{shardId, shardNumber});
        data.addIfNotNull(PRESENCE_KEY, presence);
        data.add(INTENTS_KEY, intents);

        return data;
    }
}
