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

import me.linusdev.lapi.api.communication.http.HeaderTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpHeaders;
import java.util.Optional;
import java.util.OptionalLong;

public class RateLimitHeaders {

    private final long limit;
    private final long remaining;
    private final long resetMillis;
    private final @NotNull String bucket;

    public RateLimitHeaders(long limit, long remaining, long resetMillis, @NotNull String bucket) {
        this.limit = limit;
        this.remaining = remaining;
        this.resetMillis = resetMillis;
        this.bucket = bucket;
    }

    public static @Nullable RateLimitHeaders of(@NotNull HttpHeaders headers) {

        long limit;
        long remaining;
        long resetMillis;
        String bucket;

        OptionalLong opLimit = headers.firstValueAsLong(HeaderTypes.X_RATE_LIMIT_LIMIT.getName());
        if(opLimit.isEmpty()) return null;
        limit = opLimit.getAsLong();

        OptionalLong opRemaining = headers.firstValueAsLong(HeaderTypes.X_RATE_LIMIT_REMAINING.getName());
        if(opRemaining.isEmpty()) return null;
        remaining = opRemaining.getAsLong();

        OptionalLong opReset = headers.firstValueAsLong(HeaderTypes.X_RATE_LIMIT_RESET.getName());
        if(opReset.isEmpty()) return null;
        resetMillis = opReset.getAsLong();

        Optional<String> opBucket = headers.firstValue(HeaderTypes.X_RATE_LIMIT_BUCKET.getName());
        if(opBucket.isEmpty()) return null;
        bucket = opBucket.get();


        return new RateLimitHeaders(limit, remaining, resetMillis, bucket);
    }

    public long getLimit() {
        return limit;
    }

    public long getRemaining() {
        return remaining;
    }

    public long getResetMillis() {
        return resetMillis;
    }

    public @NotNull String getBucket() {
        return bucket;
    }
}
