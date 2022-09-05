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

package me.linusdev.lapi.api.communication.gateway.websocket;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Nullable;

/**
 * The Compression Discord should use, when sending us {@link me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract payloads}.<br>
 * This is not supported by LApi as standard. You will have to implement your own converter.
 */
public enum GatewayCompression implements SimpleDatable {

    NONE(null),

    /**
     * @see <a href="https://discord.com/developers/docs/topics/gateway#payload-compression" target="_top">Payload Compression</a>
     */
    PAYLOAD_COMPRESSION(null),

    /**
     * @see <a href="https://discord.com/developers/docs/topics/gateway#transport-compression" target="_top">Transport Compression</a>
     */
    ZLIB_STREAM("zlib-stream"),

    ;

    private final String value;

    GatewayCompression(String value) {
        this.value = value;
    }

    /**
     *
     * @param value string
     * @return {@link GatewayCompression} matching given string (ignores case) or {@code null} if none matches
     */
    public static @Nullable GatewayCompression fromValue(String value){
        if(value == null) return null;
        for(GatewayCompression compression : GatewayCompression.values()){
            if(compression.value.equalsIgnoreCase(value)) return compression;
        }

        return null;
    }

    public @Nullable String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
