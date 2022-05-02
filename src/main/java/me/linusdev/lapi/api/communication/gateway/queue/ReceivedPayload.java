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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ReceivedPayload {

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

    public @NotNull GatewayPayloadAbstract getPayload() {
        return payload;
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
}
