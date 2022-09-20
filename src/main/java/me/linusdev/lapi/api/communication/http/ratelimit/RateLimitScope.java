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

package me.linusdev.lapi.api.communication.http.ratelimit;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/rate-limits#header-format">Discord Documentation</a>
 */
public enum RateLimitScope implements SimpleDatable {

    UNKNOWN(""),

    /**
     * per bot or user limit.
     */
    USER("user"),

    /**
     * per bot or user global limit.
     */
    GLOBAL("global"),

    /**
     * per resource limit.
     */
    SHARED("shared"),
    ;

    private final static RateLimitScope[] values = values();

    private final @NotNull String value;

    @Contract("null -> null; !null -> !null")
    public static @Nullable RateLimitScope of(@Nullable String value) {
        if(value == null) return null;

        for(RateLimitScope scope : values) {
            if(scope.value.equals(value)) {
                return scope;
            }
        }

        return UNKNOWN;
    }

    RateLimitScope(@NotNull String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
