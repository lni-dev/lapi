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

package me.linusdev.lapi.api.communication.gateway.other;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
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
    public static @Nullable GetGatewayResponse fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String url = (String) data.get(URL_KEY);
        Number shards = (Number) data.get(SHARDS_KEY);
        SOData sessionStartLimit = (SOData) data.get(SESSION_START_LIMIT_KEY);

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
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(URL_KEY, url);
        data.add(SHARDS_KEY, shards);
        data.add(SESSION_START_LIMIT_KEY, sessionStartLimit);

        return data;
    }
}
