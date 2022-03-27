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

package me.linusdev.lapi.api.communication.gateway.websocket;

import me.linusdev.data.SimpleDatable;
import me.linusdev.data.converter.ExceptionConverter;
import org.jetbrains.annotations.Nullable;

/**
 * Encoding, which discord should use, when sending us a {@link me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract payload}
 */
public enum GatewayEncoding implements SimpleDatable {
    /**
     * Supported by LApi as standard.<br>
     * You can implement
     * {@link me.linusdev.lapi.api.config.GatewayConfigBuilder#setJsonToPayloadConverter(ExceptionConverter) JsonToPayloadConverter}
     */
    JSON("json"),

    /**
     * Not supported by LApi.<br>
     * You <b>must</b> implement {@link me.linusdev.lapi.api.config.GatewayConfigBuilder#setBytesToPayloadConverter(ExceptionConverter) BytesToPayloadConverter}
     */
    ETF("etf"),
    ;

    private final String value;

    GatewayEncoding(String value) {
        this.value = value;
    }

    /**
     *
     * @param value string
     * @return {@link GatewayEncoding} matching given string (ignores case) or {@code null} if none matches
     */
    public static @Nullable GatewayEncoding fromValue(@Nullable String value){
        if(value == null) return null;
        for(GatewayEncoding e : GatewayEncoding.values()){
            if(e.value.equalsIgnoreCase(value)) return e;
        }

        return null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
