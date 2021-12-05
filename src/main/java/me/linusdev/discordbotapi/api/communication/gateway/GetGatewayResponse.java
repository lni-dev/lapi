package me.linusdev.discordbotapi.api.communication.gateway;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway-bot-json-response" target="_top">Get Gateway json response</a>
 */
public class GetGatewayResponse implements Datable {

    public static final String URL_KEY = "url";
    public static final String SHARDS_KEY = "shards";
    public static final String SESSION_START_LIMIT_KEY = "session_start_limit";

    private final @NotNull String url;
    private final int shards;
    private final @NotNull SessionStartLimit sessionStartLimit;

    /**
     *
     * @param url The WSS URL that can be used for connecting to the gateway
     * @param shards The recommended number of shards to use when connecting
     * @param sessionStartLimit Information on the current session start limit
     */
    public GetGatewayResponse(@NotNull String url, int shards, @NotNull SessionStartLimit sessionStartLimit) {
        this.url = url;
        this.shards = shards;
        this.sessionStartLimit = sessionStartLimit;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable GetGatewayResponse fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String url = (String) data.get(URL_KEY);
        Number shards = (Number) data.get(SHARDS_KEY);
        Data sessionStartLimit = (Data) data.get(SESSION_START_LIMIT_KEY);

        if(url == null || shards == null || sessionStartLimit == null){
            InvalidDataException.throwException(data, null, GetGatewayResponse.class,
                    new Object[]{url, shards, sessionStartLimit},
                    new String[]{URL_KEY, SHARDS_KEY, SESSION_START_LIMIT_KEY});
        }

        //noinspection ConstantConditions
        return new GetGatewayResponse(url, shards.intValue(), SessionStartLimit.fromData(sessionStartLimit));
    }

    /**
     * The WSS URL that can be used for connecting to the gateway
     */
    public @NotNull String getUrl() {
        return url;
    }

    /**
     * The recommended number of shards to use when connecting
     */
    public int getShards() {
        return shards;
    }

    /**
     * Information on the current session start limit
     */
    public @NotNull SessionStartLimit getSessionStartLimit() {
        return sessionStartLimit;
    }

    @Override
    public Data getData() {
        Data data = new Data(3);

        data.add(URL_KEY, url);
        data.add(SHARDS_KEY, shards);
        data.add(SESSION_START_LIMIT_KEY, sessionStartLimit);

        return data;
    }
}
