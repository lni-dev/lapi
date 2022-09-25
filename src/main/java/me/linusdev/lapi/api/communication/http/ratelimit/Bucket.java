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
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import org.jetbrains.annotations.NotNull;


public class Bucket {

    private final @NotNull Object limitLock = new Object();
    private volatile long limit;
    private volatile long remaining;
    private volatile long resetMillis;
    private final @NotNull String bucket;

    public Bucket(@NotNull String bucket, @NotNull RateLimitHeaders headers, @NotNull LApiHttpResponse response) {
        this.bucket = bucket;
        this.limit = headers.getLimit();
        this.remaining = headers.getRemaining();
        this.resetMillis = headers.getResetMillis();
    }

    /**
     *
     * @param future {@link QueueableFuture} to send.
     * @return {@code true} if given future can be sent. {@code false} it will be added to a queue.
     */
    public boolean canSendOrAddToQueue(@NotNull QueueableFuture<?> future) {
        synchronized (limitLock){
            if(remaining >= 1) {
                remaining--;
                return true;
            }
        }

        //TODO: add to rate limit queue
        return false;
    }
}
