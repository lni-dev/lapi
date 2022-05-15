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

package me.linusdev.lapi.api.communication.gateway.queue;

import me.linusdev.data.AbstractData;
import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.other.GatewayPayload;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ReceivedPayload implements Datable {

    public static final String PAYLOAD_KEY = "payload";
    public static final String TIME_KEY = "time";
    public static final String GUILD_ID_KEY = "guild_id";

    private final @NotNull GatewayPayloadAbstract payload;
    private final long time;
    private final @Nullable String guildId;

    public ReceivedPayload(@NotNull GatewayPayloadAbstract payload) {
        this.payload = payload;
        this.time = System.currentTimeMillis();

        if(payload.getPayloadData() != null && payload.getPayloadData() instanceof SOData) {
            this.guildId = (String) ((SOData) payload.getPayloadData()).get(GatewayWebSocket.GUILD_ID_KEY);
        } else {
            this.guildId = null;
        }
    }

    public ReceivedPayload(@NotNull GatewayPayloadAbstract payload, long time, @Nullable String guildId) {
        this.payload = payload;
        this.time = time;
        this.guildId = guildId;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable ReceivedPayload fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        GatewayPayloadAbstract payload = data.getAndConvertWithException(PAYLOAD_KEY,
                convertible -> GatewayPayload.fromData(data), null);
        long time = ((Number) data.getOrDefaultBoth(TIME_KEY, 0L)).longValue();
        String guildId = (String) data.get(GUILD_ID_KEY);

        if(payload == null) throw new InvalidDataException(data, "payload may not be null!");

        return new ReceivedPayload(payload, time, guildId);
    }

    public @NotNull GatewayPayloadAbstract getPayload() {
        return payload;
    }

    public @Nullable GatewayEvent getType() {
        return payload.getType();
    }

    public long getTime() {
        return time;
    }

    public @Nullable String getGuildId() {
        return guildId;
    }

    public boolean isFromGuild() {
        return guildId != null;
    }

    @Override
    public AbstractData<?, ?> getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(PAYLOAD_KEY, payload);
        data.add(TIME_KEY, time);
        data.add(GUILD_ID_KEY, guildId);

        return data;
    }
}
