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

import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.communication.http.HeaderTypes;
import me.linusdev.lapi.api.communication.http.queue.RateLimitQueue;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.interfaces.Unique;
import me.linusdev.lapi.api.lapi.LApiImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitManager {

    private final @NotNull LApiImpl lApi;

    private final @NotNull Object sharedResourceLock = new Object();
    private @NotNull Map<Unique, RateLimitQueue> sharedResourceRateLimits;
    private volatile boolean hasSharedResourceRateLimit;


    private @NotNull Map<Unique, RateLimitQueue> userRateLimit;


    public RateLimitManager(@NotNull LApiImpl lApi) {
        this.lApi = lApi;
        sharedResourceRateLimits = new ConcurrentHashMap<>();
        hasSharedResourceRateLimit = false;

        userRateLimit = null;
    }

    public void onRateLimit(@NotNull LApiHttpResponse response, @NotNull QueueableFuture<?> rateLimitedFuture, int retryAmount, double retryIn) {

        if(response.getRateLimitScope() == RateLimitScope.SHARED) {
            synchronized (sharedResourceLock) {
                hasSharedResourceRateLimit = true;
            }
        }


    }

    public boolean hasSharedResourceRateLimit() {
        return hasSharedResourceRateLimit;
    }
}
