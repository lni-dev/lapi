/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.communication.gateway.events.voice;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     A null endpoint means that the voice server allocated has gone away and is trying to be reallocated. You should
 *     attempt to disconnect from the currently connected voice server, and not attempt to reconnect until
 *     a new voice server is allocated.
 * </p>
 * @see <a href="https://discord.com/developers/docs/topics/gateway#voice-server-update" target="_top">Discord Documentation</a>
 */
public class VoiceServerUpdateEvent extends Event {

    public static final String TOKEN_KEY = "token";
    public static final String ENDPOINT_KEY = "endpoint";

    private final @NotNull String token;
    private final @Nullable String endpoint;

    public VoiceServerUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId, @NotNull String token, @Nullable String endpoint) {
        super(lApi, payload, guildId);
        this.token = token;
        this.endpoint = endpoint;
    }

    /**
     * voice connection token
     */
    public @NotNull String getToken() {
        return token;
    }

    /**
     * the voice server host
     */
    public @Nullable String getEndpoint() {
        return endpoint;
    }
}
