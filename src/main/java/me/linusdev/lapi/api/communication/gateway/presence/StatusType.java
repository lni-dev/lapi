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

package me.linusdev.lapi.api.communication.gateway.presence;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#update-presence-status-types" target="_top">Status Types</a>
 */
public enum StatusType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN("unknown"),

    /**
     * Online
     */
    ONLINE("online"),

    /**
     * Do Not Disturb
     */
    DND("dnd"),

    /**
     * AFK
     */
    IDLE("idle"),

    /**
     * Invisible and shown as offline
     */
    INVISIBLE("invisible"),

    /**
     * Offline
     */
    OFFLINE("offline"),
    ;

    private final String value;

    StatusType(String value) {
        this.value = value;
    }

    /**
     *
     * @param value {@link String}
     * @return {@link StatusType} matching given value or {@link #UNKNOWN} if none matches
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable StatusType fromValue(@Nullable String value){
        if(value == null) return null;
        for(StatusType type : StatusType.values()){
            if(type.value.equalsIgnoreCase(value)) return type;
        }

        return UNKNOWN;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
