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

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @see <a href="https://discord.com/developers/docs/topics/rate-limits#exceeding-a-rate-limit-rate-limit-response-structure">Discord Documentation</a>
 */
public class RateLimitResponse implements Datable {

    public final static String MESSAGE_KEY = "message";
    public final static String RETRY_AFTER_KEY = "retry_after";
    public final static String GLOBAL_KEY = "global";

    private final @NotNull String message;
    private final double retryAfter;
    private final long retryAtMillis;
    private final boolean global;

    public RateLimitResponse(@NotNull String message, double retryAfter, boolean global) {
        this.message = message;
        this.retryAfter = retryAfter;
        this.retryAtMillis = System.currentTimeMillis() + Double.valueOf(retryAfter * 1000d).longValue();
        this.global = global;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable RateLimitResponse fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String message = (String) data.get(MESSAGE_KEY);
        double retryAfter = data.getNumberAsDouble(RETRY_AFTER_KEY, key -> -1d);
        Boolean global = (Boolean) data.get(GLOBAL_KEY);

        if(message == null || retryAfter < 0d || global == null) {
            InvalidDataException.throwException(data, null, RateLimitResponse.class,
                    new Object[]{message, retryAfter < 0d ? null : retryAfter, global},
                    new String[]{MESSAGE_KEY, RETRY_AFTER_KEY, GLOBAL_KEY});
            return null; //unreachable
        }

        return new RateLimitResponse(message, retryAfter, global);
    }


    public @NotNull String getMessage() {
        return message;
    }

    /**
     * The number of seconds to wait before submitting another request.
     */
    public double getRetryAfter() {
        return retryAfter;
    }

    public long getRetryAtMillis() {
        return retryAtMillis;
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(MESSAGE_KEY, message);
        data.add(RETRY_AFTER_KEY, retryAfter);
        data.add(GLOBAL_KEY, global);

        return data;
    }
}
